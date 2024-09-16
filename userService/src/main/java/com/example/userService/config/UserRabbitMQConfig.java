package com.example.userService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
//@EnableRabbit
public class UserRabbitMQConfig {

    public static final String USER_CREATION_QUEUE = "user-creation-queue";
    public static final String USER_EXCHANGE = "user_exchange";
    public static final String USER_ROUTING_KEY = "user_routing_key";

//    private String replyText = "Message returned";
//    private String replyCode = "Reply Code";
//    private String exchange = "Exchange";
//    private String routingKey = "Routing Key";

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(UserRabbitMQConfig.USER_CREATION_QUEUE,true);
    }

    @Bean
    public Binding binding(Queue userQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with(USER_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(new Jackson2JsonMessageConverter());
//
//        // Enable mandatory flag for return callback
//        template.setMandatory(true);
//
//        // Confirm callback for successful delivery to the exchange
//        template.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                System.out.println("Message successfully delivered to RabbitMQ");
//            } else {
//                System.err.println("Failed to deliver message to RabbitMQ: " + cause);
//            }
//        });
//
//        // Return callback for failed message routing to queue
//        template.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
//            @Override
//            public void returnedMessage(ReturnedMessage returnedMessage) {
//                System.err.println(replyText);
//                System.err.println(replyCode);
//                System.err.println(exchange);
//                System.err.println(routingKey);
//            }
//        });
//
//        return template;
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());  // Use JSON converter for complex objects
        return template;
    }


    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);  // Auto-configure the factory
        factory.setMessageConverter(messageConverter());  // Set message converter to JSON
        return factory;
    }
}

