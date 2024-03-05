package com.notif_back_end.notifbackend;

import com.azure.spring.messaging.implementation.annotation.EnableAzureMessaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAzureMessaging
public class NotifBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifBackendApplication.class, args);
    }

}
