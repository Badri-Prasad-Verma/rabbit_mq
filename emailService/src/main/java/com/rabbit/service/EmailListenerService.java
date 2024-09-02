package com.rabbit.service;

import com.rabbit.config.RabbitMQConfig;
import com.rabbit.entity.User;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailListenerService {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.USER_CREATION_QUEUE, ackMode = "MANUAL")
    public void receiveUserCreationMessage(User user, Channel channel, Message message) throws IOException {
        try {
            // Process the message (e.g., send email)
            emailService.sendEmail(user.getEmail(), "Welcome " + user.getName(),
                    "Thank you for registering, " + user.getName() + "!");

            // Acknowledge message after successful processing
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // Log error and optionally reject or requeue the message
            System.err.println("Error processing message: " + e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
