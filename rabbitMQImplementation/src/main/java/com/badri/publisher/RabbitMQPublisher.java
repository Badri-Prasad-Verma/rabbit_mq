package com.badri.publisher;

import com.badri.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQPublisher {

    private static final Logger LOGGER= LoggerFactory.getLogger(RabbitMQPublisher.class);
    private RabbitTemplate rabbitTemplate;
    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message){
        LOGGER.info("message send to rabbitmq",message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE,RabbitMQConfig.ROUTING_KEY,message);
    }
}
