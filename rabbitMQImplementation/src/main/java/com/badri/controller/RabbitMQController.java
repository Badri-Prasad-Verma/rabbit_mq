package com.badri.controller;

import com.badri.publisher.RabbitMQPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class RabbitMQController {

    private RabbitMQPublisher rabbitMQPublisher;

    public RabbitMQController(RabbitMQPublisher rabbitMQPublisher) {
        this.rabbitMQPublisher = rabbitMQPublisher;
    }

    private static final Logger LOGGER= LoggerFactory.getLogger(RabbitMQController.class);
    @GetMapping("/publish")
    public void sendMessages(@RequestParam("message") String message){
        rabbitMQPublisher.sendMessage(message);
        LOGGER.info("message send successfully");
    }
}
