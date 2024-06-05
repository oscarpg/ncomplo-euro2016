package org.jgayoso.ncomplo.business.services.emailproviders;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.jgayoso.ncomplo.business.entities.ForgotPasswordToken;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.User;
import org.jgayoso.ncomplo.business.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TrustifyEmailService implements EmailService {

    private static final Logger logger = Logger.getLogger(TrustifyEmailService.class);

    private static final String KEY_HEADER = "x-trustifi-key";
    private static final String KEY_SECRET = "x-trustifi-secret";
    private static final String CONTENT_TYPE = "content-type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    public static final String NEW_PASSWORD_SUBJECT = "Your new NComplo password";
    public static final String RESTORE_PASSWORD_SUBJECT = "Restore you NComplo password";

    @Value("${trustify.url}")
    private String trustifyUrl;
    @Value("${trustify.key}")
    private String trustifyKey;
    @Value("${trustify.secret}")
    private String trustifySecret;
    @Value("${ncomplo.fromEmail}")
    private String fromEmail;

    @Value("${ncomplo.email.trustify.enabled}")
    private boolean enabled;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    protected MessageSource resource;

    public TrustifyEmailService() {
        super();
    }

    @Override
    public boolean isEnabled() {
        logger.info("TrustifyEmailService service is enabled:  " + enabled);
        return enabled;
    }

    @Override
    public void sendNewPassword(User user, String newPassword, String baseUrl) {
        if (!isEnabled()) {
            return;
        }

        final String html = "Hello " + user.getName()
                + "<br />To access to your ncomplo account, use your new credentials:<br><ul><li>Login: "
                + user.getLogin() + "</li><li>Password: " + newPassword + "</li></ul>Please, change your password!"
                + "<br /> See you soon at <a href='" + baseUrl + "'>ncomplo</a>";

        logger.info("Sending new password email to " + user.getEmail());

        try {
            sendMailRequest(Collections.singletonList(user.getEmail()), NEW_PASSWORD_SUBJECT, html);
        } catch (IOException e) {
            logger.error("Error sending password to " + user.getEmail(), e);
            return;
        }

        logger.info("Reset password email sent to " + user.getEmail());
    }

    @Override
    public void sendForgotPassword(User user, ForgotPasswordToken fpt, String url) {
        if (!isEnabled()) {
            return;
        }
        final String html = "Hello " + user.getName()
                + "<br />You have requested to restore the NComplo password for the login <b>" + user.getLogin() + "</b>."
                + "<br />If you have not requested that, ignore this email."
                + "<br />To create a new password, follow this <a href='" + url + "'>link</a>.";

        logger.info("Sending forgot password email to " + user.getEmail());
        try {
            sendMailRequest(Collections.singletonList(user.getEmail()), RESTORE_PASSWORD_SUBJECT, html);
        } catch (IOException e) {
            logger.error("Error sending forgot password to " + user.getEmail(), e);
            return;
        }
        logger.info("Forgot password email sent to " + user.getEmail());
    }

    @Override
    public void sendInvitations(String leagueName, Invitation invitation, String registerUrl, User user, Locale locale) {
        if (!isEnabled()) {
            return;
        }
        String[] subjectParams = {leagueName};
        final String emailSubject = resource.getMessage("emails.invitation.subject", subjectParams, locale);

        final Context ctx = new Context(locale);
        ctx.setVariable("invitationName", invitation.getName());
        ctx.setVariable("leagueName", leagueName);
        ctx.setVariable("url", registerUrl);
        ctx.setVariable("isNewUser", user != null);

        final String html = this.templateEngine.process("emails/invitation", ctx);

        try {
            sendMailRequest(Collections.singletonList(user.getEmail()), emailSubject, html);
        } catch (IOException e) {
            logger.error("Error sending invitation to " + user.getEmail(), e);
            return;
        }
        logger.info("Invitation sent to " + user.getEmail());
    }

    @Override
    public void sendNotification(String subject, String[] destinations, String text) {
        if (!isEnabled()) {
            return;
        }
        try {
            sendMailRequest(Arrays.asList(destinations), subject, text);
        } catch (IOException e) {
            logger.error("Error sending notifications", e);
            return;
        }
        logger.info("Notification sent");

    }

    private void sendMailRequest(List<String> recipients, String subject, String message) throws IOException {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(trustifyUrl + "/api/i/v1/email");
            httpPost.addHeader(KEY_HEADER, trustifyKey);
            httpPost.addHeader(KEY_SECRET, trustifySecret);
            httpPost.addHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
            String recipentsBody = "{\"from\": \"" + fromEmail + "\", \"name\":\"NComplo\", \"recipients\":[RECIPENTS_PLACEHOLDER]";

            StringBuilder placeholder = new StringBuilder();
            for (String recipient: recipients) {
                if (placeholder.length()>0) {
                    placeholder.append(",");
                }
                placeholder.append("{\"email\": \"").append(recipient).append("\"}");
            }
            recipentsBody = recipentsBody.replaceFirst("RECIPENTS_PLACEHOLDER", placeholder.toString());

            StringBuilder emailBodyBuilder = new StringBuilder(recipentsBody)
                    .append(",\"title\":\"").append(subject)
                    .append("\",\"html\":\"").append(message).append("\"}");

            final StringEntity entity = new StringEntity(emailBodyBuilder.toString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                logOutput(response.getEntity().getContent());
                StatusLine sl = response.getStatusLine();
                if (sl.getStatusCode() != HttpStatus.SC_OK && sl.getStatusCode() != HttpStatus.SC_ACCEPTED
                        && sl.getStatusCode() != HttpStatus.SC_CREATED) {
                    logger.error("Error sending mail. Status code: " + sl);
                }
            }
        }

    }

    private void logOutput(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            logger.debug("Email response: " + result.toString(StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            logger.debug("Error reading response", e);
        }
    }

}
