package com.notif_back_end.notifbackend.configuration;

import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.WebPubSubServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubServiceClient {
    @Value("${webPubSub.connectionString}")
    private String connString;

    @Bean
    WebPubSubServiceClient webPubSubServiceClient(){
    return new WebPubSubServiceClientBuilder()
            .connectionString(connString)
            .hub("sample_chat")
            .buildClient();
    }
}
