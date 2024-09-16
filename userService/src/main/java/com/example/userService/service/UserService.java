package com.example.userService.service;

import com.example.userService.config.UserRabbitMQConfig;
import com.example.userService.entity.User;
import com.example.userService.repository.UserRepository;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);

    }

    public void createUser(User user) {

        saveUser(user);

        // Create a JSON-compatible map with user data
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("id", user.getId());
        userMessage.put("email", user.getEmail());
        userMessage.put("name", user.getName());

        // Send user data to RabbitMQ after saving to the database
        rabbitTemplate.convertAndSend(
                UserRabbitMQConfig.USER_EXCHANGE,
                UserRabbitMQConfig.USER_ROUTING_KEY,
                userMessage,
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);  // Set persistent delivery
                    return message;
                }
        );
        System.out.println("User creation event sent to RabbitMQ for user: " + user.getEmail());
    }

    private void saveUser(User user) {
        // Save user to the database
        userRepository.save(user);
        System.out.println("User saved to database: " + user.getEmail());
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

