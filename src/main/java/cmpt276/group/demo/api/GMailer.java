package cmpt276.group.demo.api;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import cmpt276.group.demo.models.event.Event;
import cmpt276.group.demo.models.patient.Patient;

@Component
public class GMailer {

    private static final String APPLICATION_NAME = "HealthAce healthcare service";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "/gmail_credential.json";
    private static final String REDIRECT_URI = "http://localhost:8080/oauth2callback";
    
    private static final Logger logger = LoggerFactory.getLogger(GMailer.class);

    private Credential credentials;

      /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
        // if have the token already -> no need to take
        if (credentials != null) {
            return credentials;
        }

        // load client secret
        InputStream in = GMailer.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return flow.loadCredential("user");
    }

    /**
     * Generate the authorization URL to start the OAuth2 flow.
     */
    public String getAuthorizationUrl() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = GMailer.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    /**
     * Store the authorization code obtained from the OAuth2 flow and create credentials.
     */
    public void storeAuthorizationCode(String authorizationCode) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = GMailer.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        GoogleTokenResponse tokenResponse = flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
        credentials = flow.createAndStoreCredential(tokenResponse, "user");
    }

    /**
     * Refresh the access token if it has expired.
     */
    private void refreshCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
        if (credentials.getExpiresInSeconds() <= 60) {
            GoogleRefreshTokenRequest refreshTokenRequest = new GoogleRefreshTokenRequest(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    credentials.getRefreshToken(),
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GMailer.class.getResourceAsStream(CREDENTIALS_FILE_PATH))).getDetails().getClientId(),
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GMailer.class.getResourceAsStream(CREDENTIALS_FILE_PATH))).getDetails().getClientSecret()
            );
            GoogleTokenResponse tokenResponse = refreshTokenRequest.execute();
            credentials.setAccessToken(tokenResponse.getAccessToken());
        }
    }

    /**
     * Build and return a Gmail service instance using the stored credentials.
     */
    public Gmail getService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        credentials = getCredentials(HTTP_TRANSPORT);
        refreshCredentials(HTTP_TRANSPORT);
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    
    public static MimeMessage createBookEmail(Patient patient, Event event) throws Exception {
        String to = patient.getUsername();
        String subject = "Confirmation registering for " + event.getEventName();
        String body = "You are successfully registered for " + event.getEventName() +
                        ".\nThis event will be on " + event.getDate() + " at " + event.getStartTime() +
                        ".\nThank you for registering for our consultation event" +
                        ".\nBest regards, \nHealthAce Service";

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress("healthace.donotreply@gmail.com"));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(body);
        return email;
    }

    
    public static MimeMessage createDeleteEmail(Patient patient, Event event) throws Exception {
        String to = patient.getUsername();
        String subject = "Confirmation of unbooking for " + event.getEventName();
        String body = "You have successfully unbooked from " + event.getEventName() + 
                      ".\nThis event was scheduled for " + event.getDate() + " at " + event.getStartTime() +
                      ".\nThank you for letting us know" + 
                      ".\nBest regards, \nHealthAce Service";

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress("healthace.donotreply@gmail.com"));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(body);
        return email;
    }

   
    public static void sendEmail(Gmail service, MimeMessage emailContent) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        service.users().messages().send("me", message).execute();
    }

    
    public void sendBookMail(Patient patient, Event event) throws Exception {
        Gmail service = getService();
        MimeMessage emailContent = createBookEmail(patient, event);
        sendEmail(service, emailContent);
    }

    
    public void sendDeleteMail(Patient patient, Event event) throws Exception {
        Gmail service = getService();
        MimeMessage emailContent = createDeleteEmail(patient, event);
        sendEmail(service, emailContent);
    }

}
