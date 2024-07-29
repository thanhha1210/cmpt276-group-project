package cmpt276.group.demo.controllers;

import cmpt276.group.demo.api.GMailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api")
public class ChatbotController {

    @Value("${openai.api.key}")
    private String apiKey;

    @Autowired
    private GMailer gMailer;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are a doctor. Answer the user's questions with medical advice."),
                Map.of("role", "user", "content", userMessage)
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        if (response == null || !response.containsKey("choices")) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> messageContent = (Map<String, Object>) firstChoice.get("message");

        String assistantMessage = (String) messageContent.get("content");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", assistantMessage);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize() {
        try {
            String authorizationUrl = gMailer.getAuthorizationUrl();
            return ResponseEntity.ok(authorizationUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate authorization URL.");
        }
    }

    @PostMapping("/oauth2callback")
    public ResponseEntity<String> oauth2callback(@RequestParam("code") String authorizationCode) {
        try {
            gMailer.storeAuthorizationCode(authorizationCode);
            return ResponseEntity.ok("Authorization successful. You can now send emails.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store authorization code.");
        }
    }

    @PostMapping("/sendChatLog")
    public ResponseEntity<Map<String, Object>> sendChatLog(@RequestBody Map<String, Object> requestBody) {
        String email = (String) requestBody.get("email");
        List<Map<String, String>> chatLog = (List<Map<String, String>>) requestBody.get("chatLog");

        // Construct chat log content
        StringBuilder chatContent = new StringBuilder();
        for (Map<String, String> message : chatLog) {
            chatContent.append(message.get("sender")).append(": ").append(message.get("message")).append("\n");
        }

        // Send email using GMailer
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage emailContent = new MimeMessage(session);
            emailContent.setFrom(new InternetAddress("healthace.donotreply@gmail.com"));
            emailContent.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));
            emailContent.setSubject("Your HealthAce Chat Log");
            emailContent.setText(chatContent.toString());

            gMailer.sendEmail(gMailer.getService(), emailContent);
            return new ResponseEntity<>(Map.of("success", true), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("success", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
