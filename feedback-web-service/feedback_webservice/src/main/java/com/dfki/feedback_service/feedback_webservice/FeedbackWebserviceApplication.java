package com.dfki.feedback_service.feedback_webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FeedbackWebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedbackWebserviceApplication.class, args);
        System.out.println("Server running...");
    }

}
