package com.example.userService.service;

import com.example.userService.config.UserRabbitMQConfig;
import com.example.userService.entity.User;
import com.example.userService.repository.UserRepository;
import com.example.userService.util.CSVGenerator;
import com.example.userService.util.ExcelGenerator;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

        List<User> users = getAllUsers();

        try {
            // Generate Excel file
            ByteArrayOutputStream excelStream = ExcelGenerator.usersToExcel(users);
            System.out.println("Excel file generated for users.");

            // Convert ByteArrayOutputStream to ByteArrayInputStream
            ByteArrayInputStream excelInputStream = new ByteArrayInputStream(excelStream.toByteArray());

            // Generate CSV file
            String csvData = CSVGenerator.usersToCSV(users);
            System.out.println("CSV file generated for users.");

            // Prepare the message for RabbitMQ
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("email", user.getEmail());
            emailData.put("subject", "User Creation Confirmation");
            emailData.put("message", "User created successfully. Attached is the list of all users.");
            emailData.put("csvData", csvData); // Add CSV data for email-service to handle
            emailData.put("fileNameCSV", "users.csv");
            emailData.put("excelData", excelStream.toByteArray()); // Add Excel data as byte array
            emailData.put("fileNameExcel", "users.xlsx"); // Filename for Excel

            // Send the message to RabbitMQ
            rabbitTemplate.convertAndSend(
                    UserRabbitMQConfig.USER_EXCHANGE,
                    UserRabbitMQConfig.USER_ROUTING_KEY,
                    emailData,
                    message -> {
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    }
            );

            System.out.println("User creation event sent to RabbitMQ for user: " + user.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUser(User user) {
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
