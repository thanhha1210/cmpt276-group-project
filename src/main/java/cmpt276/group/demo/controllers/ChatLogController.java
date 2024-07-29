package cmpt276.group.demo.controllers;

import cmpt276.group.demo.api.GMailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ChatLogController {
    @Autowired
    private GMailer gMailer;
    
    public MimeMessage createEmail(String to, String subject, String bodyText) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress("healthace.donotreply@gmail.com"));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }
    
    @PostMapping("/sendChatLog")
    public Map<String, Object> sendChatLog(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        List<Map<String, String>> chatLog = (List<Map<String, String>>) request.get("chatLog");

        StringBuilder chatLogContent = new StringBuilder("Chat Log:\n\n");
        for (Map<String, String> message : chatLog) {
            chatLogContent.append(message.get("sender")).append(": ").append(message.get("message")).append("\n");
        }

        try {
            MimeMessage emailContent = createEmail(email, "Chat Log", chatLogContent.toString());
            gMailer.sendEmail(gMailer.getService(), emailContent);
            return Map.of("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false);
        }
    }
}
