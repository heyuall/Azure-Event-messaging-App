package com.notif_back_end.notifbackend.controller;

//import com.notif_back_end.notifbackend.service.event_hubs.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Supplier;

@RestController
@RequestMapping("/event")
@CrossOrigin("*")
public class EventHubsCotroller {

    private final Sinks.Many<Message<String>> many;
    private final Supplier<Flux<Message<String>>> supply;
    private final Consumer<Message<String>> consume;
    private final Logger log = LoggerFactory.getLogger(EventHubsCotroller.class);

    public EventHubsCotroller(Sinks.Many<Message<String>> many, Supplier<Flux<Message<String>>> supply, Consumer<Message<String>> consume) {
        this.many = many;
        this.supply = supply;
        this.consume = consume;
    }

    @GetMapping("/pushToStream")
    ResponseEntity<String> pushToStream(@RequestParam("message") String message) {
        log.info("Going to add message {} to sendMessage.", message);
		many.emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);

        return ResponseEntity.ok("The following message was sent: " + message);
    }

}
