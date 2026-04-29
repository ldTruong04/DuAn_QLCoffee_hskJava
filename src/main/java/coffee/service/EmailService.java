package coffee.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    public static void sendOTP(String toEmail, String otp) throws Exception {
        String fromEmail = "ledinhtruong2004@gmail.com";
        String password = "aszd qtib oiaj kxsw";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(toEmail)
        );

        message.setSubject("OTP Reset Password");
        message.setText("Mã OTP của bạn là: " + otp);

        Transport.send(message);

        System.out.println("Gửi email thành công!");
    }
}