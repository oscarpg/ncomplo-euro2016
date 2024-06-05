package org.jgayoso.ncomplo.business.services.emailproviders;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class MailgunEmailService implements EmailService {

    private static final Logger logger = Logger.getLogger(MailgunEmailService.class);

    public static final String NEW_PASSWORD_SUBJECT = "Your new NComplo password";
    public static final String RESTORE_PASSWORD_SUBJECT = "Restore you NComplo password";

    @Value("${mailgun.domain}")
    private String domain;
    @Value("${mailgun.apikey}")
    private String apikey;
    @Value("${ncomplo.fromEmail}")
    private String fromEmail;

    @Value("${ncomplo.email.mailgun.enabled}")
    private boolean enabled;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    protected MessageSource resource;


    @Override
    public boolean isEnabled() {
        logger.info("MailgunEmailService service is enabled:  " + enabled);
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
        try {
            for (String recipient: recipients) {
                HttpResponse request = Unirest.post("https://api.mailgun.net/v3/" + domain + "/messages")
                        .basicAuth("api", apikey)
                        .queryString("from", "NComplo <" + fromEmail + ">")
                        .queryString("to", recipient)
                        .queryString("subject", subject)
                        .body(message)
                        .asJson();
                logger.debug("email sent to " + recipient + ". response: " + request.getBody().toString());
            }
        } catch (UnirestException e) {
            throw new IOException("Error reading response", e);
        }
    }


}
