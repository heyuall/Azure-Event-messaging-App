package com.notif_back_end.notifbackend.configuration;

import com.azure.spring.messaging.checkpoint.Checkpointer;
import com.azure.spring.messaging.eventhubs.support.EventHubsHeaders;
import com.notif_back_end.notifbackend.service.NotificationManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.azure.spring.messaging.AzureHeaders.CHECKPOINTER;

@Configuration
public class EventHubsCloudStreamConfig {
    private static final Logger log = LoggerFactory.getLogger(EventHubsCloudStreamConfig.class);

    @Autowired
    private NotificationManagerService notificationManagerService;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Bean
    public Sinks.Many<Message<String>> many() {
        log.info(("INIT MANY"));

        return Sinks.many().unicast().onBackpressureBuffer();
    }


    @Bean
    public Supplier<Flux<Message<String>>> supply(Sinks.Many<Message<String>> many) {
        log.info(("INIT SUPPLY"));

        return () -> many.asFlux()
                .doOnNext(m -> log.info("Manually sending message {}", m))
                .doOnError(t -> log.error("Error encountered", t));
    }

    @Bean
    public Consumer<Message<String>> consume() {
        log.info(("INIT CONSUME"));

        return message -> {
            Checkpointer checkpointer = (Checkpointer) message.getHeaders().get(CHECKPOINTER);
            log.info("New message received: '{}', partition key: {}, sequence number: {}, offset: {}, enqueued "
                            + "time: {}",
                    message.getPayload(),
                    message.getHeaders().get(EventHubsHeaders.PARTITION_KEY),
                    message.getHeaders().get(EventHubsHeaders.SEQUENCE_NUMBER),
                    message.getHeaders().get(EventHubsHeaders.OFFSET),
                    message.getHeaders().get(EventHubsHeaders.ENQUEUED_TIME)
            );
            checkpointer.success()
                    .doOnSuccess(success ->
                    {
                        log.info("Message '{}' successfully checkpointed",
                                message.getPayload()
                        );

                        Runnable runnableTask = () -> {
                            buildNotifAndSend(message.getPayload());
                        };
                        executor.execute(runnableTask);
                    })
                    .doOnError(error -> log.error("Exception found", error))
                    .block();
        };
    }

    private void buildNotifAndSend(String payload) {
        //parsing from userId,message
        String[] parts = payload.split(",");
        notificationManagerService.sendNotificationToCorrespondingUser(parts[0].trim(), parts[1].trim());
    }


}
