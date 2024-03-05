package com.notif_back_end.notifbackend.service;

import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.models.GetClientAccessTokenOptions;
import com.azure.messaging.webpubsub.models.WebPubSubClientAccessToken;
import com.azure.messaging.webpubsub.models.WebPubSubContentType;
import com.notif_back_end.notifbackend.entity.Notification;
import com.notif_back_end.notifbackend.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationManagerService {
    private final Logger logger = LoggerFactory.getLogger(NotificationManagerService.class);
    @Autowired
    UserConnectedManagerService userConnectedManagerService;
    @Autowired
    private WebPubSubServiceClient service;
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getPendingNotifications(String userId) {
        return this.notificationRepository.findByUserId(userId);
    }

    public Notification sendNotificationToCorrespondingUser(String userId, String message) {
        Notification notification = new Notification(null, message, userId);
        if (userConnectedManagerService.isConnectedUser(userId)) {
            this.service.sendToUser(userId, message, WebPubSubContentType.TEXT_PLAIN);
            return notification;
        } else {
            notification = this.notificationRepository.save(notification);
            logger.debug("saved notification with message: {}, for not connected user: {}", message, userId);
            return notification;
        }
    }

    public List<Notification> connectUserAndSendPendingNotifications(String userId) {
        this.userConnectedManagerService.connectUser(userId);
        List<Notification> notifications = getPendingNotifications(userId);
        if (!notifications.isEmpty()) {
            sendNotifications(userId, notifications);
            deleteSentNotifications(notifications, userId);
        }
        return notifications;
    }

    public void deleteSentNotifications(List<Notification> notifications, String userId) {
        this.notificationRepository.deleteAll(notifications);
        logger.debug("Deleted notifs {} for user {}",
                notifications.stream().map(Notification::getNotificationMessage)
                        .reduce("", (msg1, msg2) -> String.join(", ", msg1, msg2)),
                userId);
    }

    public void sendNotifications(String userId, List<Notification> notifications) {
        for (Notification notification : notifications) {
            service.sendToUser(userId, notification.getNotificationMessage(), WebPubSubContentType.TEXT_PLAIN);
        }
    }

    public void sendSingleNotification(String userId, String message) {
        service.sendToUser(userId, message, WebPubSubContentType.TEXT_PLAIN);
    }

    public String negotiate(String userId) {
        GetClientAccessTokenOptions option = new GetClientAccessTokenOptions();
        option.setUserId(userId);
        WebPubSubClientAccessToken token = service.getClientAccessToken(option);
        return token.getUrl();
    }

    public String disconnect(String userId) {
        this.userConnectedManagerService.deconnectUser(userId);
        return userId;
    }
}
