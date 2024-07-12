package cmpt276.group.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cmpt276.group.demo.api.GMailer;

@Controller
public class OAuth2Controller {

    @Autowired
    private GMailer gMailer;

    @GetMapping("/oauth2callback")
    public String oauth2Callback(@RequestParam String code) {
        try {
            gMailer.storeAuthorizationCode(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admins/mainPage";
    }
    @GetMapping("/admin/authorize")
    public String authorizeAdmin() {
        try {
            String authorizationUrl = gMailer.getAuthorizationUrl();
            return "redirect:" + authorizationUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "loginPage";  // Handle this appropriately in your application
    }
}