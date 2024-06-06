package org.jgayoso.ncomplo.business.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceFactory {

    private static final Logger logger = Logger.getLogger(EmailServiceFactory.class);

    @Value("${ncomplo.email.sendgrid.enabled}")
    private boolean sendgridEnabled;
    @Value("${emailservice.enabled}")
    private boolean genericEnabled;

    @Autowired
    private GenericHTTPEmailService emailService;
    @Autowired
    private SendGridEmailService sendGridEmailService;

    private boolean firstLog=true;

    public EmailService getEmailService() {
        if (genericEnabled) {
            logConfiguration(emailService);
            return emailService;
        } else if (sendgridEnabled) {
            logConfiguration(sendGridEmailService);
            return sendGridEmailService;
        }
        return null;
    }

    private void logConfiguration(EmailService emailService) {
        if (firstLog) {
            firstLog = false;
            emailService.logConfiguration();
        }

    }

}
