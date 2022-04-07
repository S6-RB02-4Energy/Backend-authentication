package com.Energy.BasicSpringAPI.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailService {

    public void sendEmailConfirmation(String email, String userName, String confirmationCode){
        final String userMailAddress = "samirzalmay1000@gmail.com"; //should come from application.properties
        final String password = "Group3RandomPass"; //should come from application.properties

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userMailAddress, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("samirzalmay1000@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("4Energy email-confirmation");
            message.setText("Dear " + userName + ","
                    + "\n\nIn order to have access to the 4Energy website your email must be confirmed."
                    + "\nUse the code under this line to fill in the website:"
                    + "\n\n   " + confirmationCode
                    + "\n\nWith kind regards,"
                    + "\n4Energy"
                    + "\n\nThis is an automated email. No response will be given on return mails.");

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void resendConfirmationCode(String email, String userName, String confirmationCode){
        final String userMailAddress = "samirzalmay1000@gmail.com"; //should come from application.properties
        final String password = "Group3RandomPass"; //should come from application.properties

        // TODO don't hard code email secrets
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userMailAddress, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("samirzalmay1000@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("4Energy resend email-confirmation");
            message.setText("Dear " + userName + ","
                    + "\n\nIn order to have access to the 4Energy website your email must be confirmed."
                    + "\nUse the new code under this line to fill in the website:"
                    + "\n\n   " + confirmationCode
                    + "\n\nWith kind regards,"
                    + "\n4Energy"
                    + "\n\nThis is an automated email. No response will be given on return mails.");

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
