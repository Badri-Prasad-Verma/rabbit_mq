package com.example.userService.util;

import com.example.userService.entity.User;
import com.opencsv.CSVWriter;

import java.io.StringWriter;
import java.util.List;

public class CSVGenerator {

    public static String usersToCSV(List<User> users) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);

        // Header
        String[] header = {"ID", "Name", "Email"};
        csvWriter.writeNext(header);

        // Data
        for (User user : users) {
            String[] data = {String.valueOf(user.getId()), user.getName(), user.getEmail()};
            csvWriter.writeNext(data);
        }

        return writer.toString();
    }
}

