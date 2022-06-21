package com.Energy.BasicSpringAPI.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Properties;

@Component
public class MailService {
    Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMalformed().ignoreIfMissing().load();

    public void sendEmailConfirmation(String email, String userName, String confirmationCode){
        final String userMailAddress = dotenv.get("EMAIL_ADDRESS");
        final String password = dotenv.get("EMAIL_PW");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", dotenv.get("SMTP_HOST"));
        prop.put("mail.smtp.port", dotenv.get("SMTP_PORT"));
        prop.put("mail.smtp.auth", dotenv.get("SMTP_AUTH"));
        prop.put("mail.smtp.starttls.enable", dotenv.get("SMTP_START_TLS"));

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userMailAddress, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Objects.requireNonNull(dotenv.get("EMAIL_ADDRESS"))));
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
        final String userMailAddress = dotenv.get("EMAIL_ADDRESS");
        final String password = dotenv.get("EMAIL_PW");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", dotenv.get("SMTP_HOST"));
        prop.put("mail.smtp.port", dotenv.get("SMTP_PORT"));
        prop.put("mail.smtp.auth", dotenv.get("SMTP_AUTH"));
        prop.put("mail.smtp.starttls.enable", dotenv.get("SMTP_START_TLS"));


        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userMailAddress, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Objects.requireNonNull(dotenv.get("EMAIL_ADDRESS"))));
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
