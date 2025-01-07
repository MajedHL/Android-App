package fr.utt.if26.tasksorganizer.Utils;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fr.utt.if26.tasksorganizer.Cred.Credentials;

public class EmailSender {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void sendEmail(String toEmail, String subject, String body) {
        executorService.execute(() -> {
            String fromEmail = Credentials.fromEmail;
            String appPassword = Credentials.password;

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, appPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }
}
