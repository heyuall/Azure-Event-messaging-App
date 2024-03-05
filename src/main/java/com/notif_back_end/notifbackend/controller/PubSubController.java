package com.notif_back_end.notifbackend.controller;

import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.notif_back_end.notifbackend.service.NotificationManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class PubSubController {
    private final WebPubSubServiceClient service;
    private final NotificationManagerService notificationManagerService;

    public PubSubController(WebPubSubServiceClient service, NotificationManagerService notificationManagerService) {
        this.service = service;
        this.notificationManagerService = notificationManagerService;
    }

    @GetMapping("/negotiate")
    ResponseEntity<String> negotiate(@RequestParam("id") String id) {
        String url = this.notificationManagerService.negotiate(id);

        return ResponseEntity.ok(url);
    }

    @PostMapping("/eventhandler")
    ResponseEntity<?> sendNotifs(@RequestHeader(value = "ce-type") String event,
                                 @RequestHeader(value = "ce-userId") String id,
                                 @RequestBody String message) {
        if ("azure.webpubsub.sys.connected".equals(event)) {
//            service.sendToAll(String.format("[SYSTEM] %s joined", id), WebPubSubContentType.TEXT_PLAIN);
//            service.sendToUser(id, String.format("notif 1"), WebPubSubContentType.TEXT_PLAIN);
            this.notificationManagerService.connectUserAndSendPendingNotifications(id);
        } else if ("azure.webpubsub.sys.disconnected".equals(event)) {
            System.out.println("event disconnect userID: "+ id);
            this.notificationManagerService.disconnect(id);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate")
    ResponseEntity<String> getNotifs(@RequestParam("id") String id,
                                     @RequestParam("message") String message) {
        this.notificationManagerService.sendNotificationToCorrespondingUser(id, message);
        return ResponseEntity.ok().build();
    }
}
