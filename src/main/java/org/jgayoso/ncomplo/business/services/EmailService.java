package org.jgayoso.ncomplo.business.services;

import org.jgayoso.ncomplo.business.entities.ForgotPasswordToken;
import org.jgayoso.ncomplo.business.entities.Invitation;
import org.jgayoso.ncomplo.business.entities.User;

import java.util.Locale;

public interface EmailService {

    boolean isEnabled();
    void sendNewPassword(final User user, final String newPassword, final String baseUrl);
    void sendForgotPassword(final User user, final ForgotPasswordToken fpt, final String url);
    void sendInvitations(final String leagueName, final Invitation invitation, final String registerUrl,
                         final User user, final Locale locale);
    void sendNotification(final String subject, final String[] destinations, final String text);
}
