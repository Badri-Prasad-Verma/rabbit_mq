package com.rabbit.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailListenerService {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = "user-creation-queue")
    public void sendEmail(Map<String, Object> emailData) {
        String email = (String) emailData.get("email");
        String subject = (String) emailData.get("subject");
        String messageText = (String) emailData.get("message");
        String csvData = (String) emailData.get("csvData"); // CSV data passed as string
        String fileName = (String) emailData.get("fileName");

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(messageText);

            // Attach CSV file
            if (csvData != null) {
                if (fileName == null || fileName.isEmpty()) {
                    fileName = "default.csv";  // Assign a default filename if it's null or empty
                }

                InputStreamSource attachment = new ByteArrayResource(csvData.getBytes());
                helper.addAttachment(fileName, attachment);
            }

            mailSender.send(message);
            System.out.println("Email sent successfully to " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Error occurred while sending email: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
