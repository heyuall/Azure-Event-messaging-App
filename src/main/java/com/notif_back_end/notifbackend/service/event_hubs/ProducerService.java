package com.notif_back_end.notifbackend.service.event_hubs;

import com.azure.spring.messaging.eventhubs.core.EventHubsTemplate;
import com.notif_back_end.notifbackend.NotifBackendApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    @Autowired
    EventHubsTemplate eventHubsTemplate;
    private static final String EVENT_HUB_NAME = "eventhubhoussem";
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifBackendApplication.class);

    public String pushMessage(String message) {
		LOGGER.info("Sending a message: {} to the Event Hubs.", message);
		eventHubsTemplate.sendAsync(EVENT_HUB_NAME, MessageBuilder.withPayload(message).build()).subscribe();
        return message;
    }
}
