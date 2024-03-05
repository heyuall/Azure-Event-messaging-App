package com.notif_back_end.notifbackend.service.event_hubs;

import com.azure.spring.messaging.eventhubs.implementation.core.annotation.EventHubsListener;
import com.notif_back_end.notifbackend.service.NotificationManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @Autowired
    private NotificationManagerService notificationManagerService;
    private static final String EVENT_HUB_NAME = "eventhubhoussem";
    private static final String CONSUMER_GROUP = "$DEFAULT";

    @EventHubsListener(destination = EVENT_HUB_NAME, group = CONSUMER_GROUP)
    public void handleMessageFromEventHub(String message) {
        System.out.printf("New message received: %s%n", message);
        notificationManagerService.sendNotificationToCorrespondingUser("user", message);
    }

}