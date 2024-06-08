package org.jgayoso.ncomplo.business.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GenericHTTPEmailService implements EmailService {

    private static final Logger logger = Logger.getLogger(GenericHTTPEmailService.class);

    private static final String NEW_PASSWORD_SUBJECT = "Your new NComplo password";
    private static final String RESTORE_PASSWORD_SUBJECT = "Restore you NComplo password";

    private String emailUrl;

    private String headers;

    private String fromEmail;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    protected MessageSource resource;

    public GenericHTTPEmailService() {
        this.emailUrl = System.getenv("EMAIL_URL");
        this.headers = System.getenv("EMAIL_HEADERS");
        this.fromEmail = System.getenv("EMAIL_FROM");
    }

    @Override
    public void logConfiguration() {
        logger.info("emailUrl: " + emailUrl);
        logger.info("headers length: " + headers.length());
        logger.info("fromEmail: " + fromEmail);

    }

    @Override
    public void sendNewPassword(User user, String newPassword, String baseUrl) {

        final String html = "Hello " + user.getName()
                + "<br />To access to your ncomplo account, use your new credentials:<br><ul><li>Login: "
                + user.getLogin() + "</li><li>Password: " + newPassword + "</li></ul>Please, change your password!"
                + "<br /> See you soon at <a href='" + baseUrl + "'>ncomplo</a>";

        logger.info("Sending new password email to " + user.getEmail());

        try {
            sendMailRequest(Collections.singletonList(new EmailIndividual(user.getEmail(),
                    user.getName())), NEW_PASSWORD_SUBJECT, html);
        } catch (IOException e) {
            logger.error("Error sending password to " + user.getEmail(), e);
            return;
        }

        logger.info("Reset password email sent to " + user.getEmail());
    }

    @Override
    public void sendForgotPassword(User user, ForgotPasswordToken fpt, String url) {

        final String html = "Hello " + user.getName()
                + "<br />You have requested to restore the NComplo password for the login <b>" + user.getLogin() + "</b>."
                + "<br />If you have not requested that, ignore this email."
                + "<br />To create a new password, follow this <a href='" + url + "'>link</a>.";

        logger.info("Sending forgot password email to " + user.getEmail());
        try {
            sendMailRequest(Collections.singletonList(new EmailIndividual(user.getEmail(),
                    user.getName())), RESTORE_PASSWORD_SUBJECT, html);
        } catch (IOException e) {
            logger.error("Error sending forgot password to " + user.getEmail(), e);
            return;
        }
        logger.info("Forgot password email sent to " + user.getEmail());

    }

    @Override
    public void sendInvitations(String leagueName, Invitation invitation, String registerUrl, User user, Locale locale) {

        String[] subjectParams = {leagueName};
        final String emailSubject = resource.getMessage("emails.invitation.subject", subjectParams, locale);

        final Context ctx = new Context(locale);
        ctx.setVariable("invitationName", invitation.getName());
        ctx.setVariable("leagueName", leagueName);
        ctx.setVariable("url", registerUrl);
        ctx.setVariable("isNewUser", user != null);

        final String html = this.templateEngine.process("emails/invitation", ctx);

        try {
            sendMailRequest(Collections.singletonList(new EmailIndividual(invitation.getEmail(),
                    invitation.getName())), emailSubject, html);
        } catch (IOException e) {
            logger.error("Error sending invitation to " + invitation.getEmail(), e);
            return;
        }
        logger.info("Invitation sent to " + invitation.getEmail());
    }

    @Override
    public void sendNotification(String subject, String[] destinations, String text) {

        try {
            List<EmailIndividual> tos = new ArrayList<>(destinations.length);
            for (String destination: destinations) {
                tos.add(new EmailIndividual(destination, null));
            }

            sendMailRequest(tos, subject, text);
        } catch (IOException e) {
            logger.error("Error sending notifications", e);
            return;
        }
        logger.info("Notification sent");
    }

    private void sendMailRequest(List<EmailIndividual> recipients, String subject, String message) throws IOException {
        Email email = new Email();
        email.setSubject(subject);
        email.setHtml(message);
        email.setFrom(new EmailIndividual(fromEmail, "NComplo"));
        email.setTo(recipients.toArray(new EmailIndividual[0]));

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(email);


        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(emailUrl);
            addHeaders(httpPost);

            final StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            logger.info("Sending email " + subject + " to emails " + recipients + " throw the API " + emailUrl);
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

    private void addHeaders(HttpPost httpPost) {
        httpPost.addHeader("content-type", ContentType.APPLICATION_JSON.toString());

        if (StringUtils.isEmpty(headers)) {
            return;
        }

        for (String header: headers.split(";")) {
            String[] nameValue = header.split(":");
            if (nameValue.length != 2) {
                continue;
            }
            httpPost.addHeader(nameValue[0], nameValue[1]);
        }
    }

    private void logOutput(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            logger.debug("Email response: " + result.toString(StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            logger.debug("Error reading response", e);
        }
    }

    static class Email {
        private EmailIndividual from;
        private EmailIndividual[] to;
        private String subject;
        private String html;

        public EmailIndividual getFrom() {
            return from;
        }

        public void setFrom(EmailIndividual from) {
            this.from = from;
        }

        public EmailIndividual[] getTo() {
            return to;
        }

        public void setTo(EmailIndividual[] to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }
    }

    static class EmailIndividual {
        private String email;
        private String name;

        public EmailIndividual(String email, String name) {
            this.email = email;
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "EmailIndividual{" +
                    "email='" + email + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


}
