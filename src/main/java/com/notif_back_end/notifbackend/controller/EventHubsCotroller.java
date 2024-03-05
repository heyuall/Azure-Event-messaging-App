package com.notif_back_end.notifbackend.controller;

import com.notif_back_end.notifbackend.service.event_hubs.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@CrossOrigin("*")
public class EventHubsCotroller {

    @Autowired
    private ProducerService producerService;

    @GetMapping("/push")
    ResponseEntity<String> push(@RequestParam("message") String message) {
        String msg = this.producerService.pushMessage(message);
        return ResponseEntity.ok(msg);
    }

}
