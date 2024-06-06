package org.jgayoso.ncomplo.business.services.emailproviders;

import org.jgayoso.ncomplo.business.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceFactory {

    @Value("${ncomplo.email.sendgrid.enabled}")
    private boolean sendgridEnabled;
    @Value("${emailservice.enabled}")
    private boolean genericEnabled;

    @Autowired
    private GenericHTTPEmailService emailService;
    @Autowired
    private SendGridEmailService sendGridEmailService;

    public EmailService getEmailService() {
        if (genericEnabled) {
            return emailService;
        } else if (sendgridEnabled) {
            return sendGridEmailService;
        }
        return null;
    }

}
