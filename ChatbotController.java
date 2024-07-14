package cmpt276.group.demo.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import cmpt276.group.demo.utils.ChatEmail;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatbotController {

  @Value("${openai.api.key}")
  private String apiKey;

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
        Map.of("role", "system", "content", "You are a doctor's assistant. Answer the user's questions with medical advice. Keep the answers short."),
        Map.of("role", "user", "content", userMessage)));

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

    if (response == null || !response.containsKey("choices")) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
    Map<String, Object> firstChoice = choices.get(0);
    Map<String, String> messageContent = (Map<String, String>) firstChoice.get("message");

    String assistantMessage = messageContent.get("content");

    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", assistantMessage);

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @PostMapping("/sendChatLog")
  public ResponseEntity<String> sendChatLog(@RequestParam String email, HttpSession session) {
      List<String> chatLog = (List<String>) session.getAttribute("chatLog");
      if (chatLog == null) {
          return new ResponseEntity<>("No chat log found.", HttpStatus.NOT_FOUND);
      }

      String chatContent = String.join("\n", chatLog);
      ChatEmail.sendEmail(email, "Your HealthAce Chat Log", chatContent);
      return new ResponseEntity<>("Chat log sent to " + email, HttpStatus.OK);
  }
}
