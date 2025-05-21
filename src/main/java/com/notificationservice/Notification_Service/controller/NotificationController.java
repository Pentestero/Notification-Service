// Fichier : notification-service/src/main/java/com/votre_groupe/notificationservice/controller/NotificationController.java
package com.votre_groupe.notificationservice.controller;

import com.votre_groupe.notificationservice.model.Notification;
import com.votre_groupe.notificationservice.service.NotificationService;
import com.votre_groupe.notificationservice.dto.NotificationRequest; // DTO (Data Transfer Object) pour les requêtes entrantes
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications") // Chemin de base pour les APIs de ce service
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Endpoint générique pour envoyer une notification (peut être appelé par n'importe quel service)
    // Exemple d'appel POST : http://localhost:8081/api/notifications/send
    // Body JSON : { "userId": 1, "type": "some_event", "title": "Event", "content": "Details", "relatedId": 123 }
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        try {
            Notification notification = notificationService.createNotification(
                notificationRequest.getUserId(),
                notificationRequest.getType(),
                notificationRequest.getTitle(),
                notificationRequest.getContent(),
                notificationRequest.getRelatedId()
            );
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log l'erreur (utiliser un logger comme SLF4J/Logback en prod)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint spécifique pour une demande de réservation (appelé par le Service de Réservation)
    // Exemple d'appel POST : http://localhost:8081/api/notifications/send/reservation-request?ownerUserId=1&requestId=101
    @PostMapping("/send/reservation-request")
    public ResponseEntity<Void> sendReservationRequestNotification(
            @RequestParam Long ownerUserId,
            @RequestParam Long requestId) {
        notificationService.sendReservationRequestNotificationToOwner(ownerUserId, requestId);
        return ResponseEntity.ok().build(); // Retourne 200 OK sans corps
    }

    // Endpoint spécifique pour une acceptation de réservation (appelé par le Service de Réservation)
    // Exemple d'appel POST : http://localhost:8081/api/notifications/send/reservation-accepted?tenantUserId=2&requestId=101
    @PostMapping("/send/reservation-accepted")
    public ResponseEntity<Void> sendReservationAcceptedNotification(
            @RequestParam Long tenantUserId,
            @RequestParam Long requestId) {
        notificationService.sendReservationAcceptedNotificationToTenant(tenantUserId, requestId);
        return ResponseEntity.ok().build();
    }

    // Endpoint pour récupérer les notifications non lues d'un utilisateur (appelé par le frontend React)
    // Exemple d'appel GET : http://localhost:8081/api/notifications/unread/1
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        if (notifications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK); // 200 OK
    }

    // Endpoint pour récupérer toutes les notifications d'un utilisateur (appelé par le frontend React)
    // Exemple d'appel GET : http://localhost:8081/api/notifications/all/{userId}
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Notification>> getAllNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getAllNotificationsForUser(userId);
        if (notifications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    // Endpoint pour marquer une notification comme lue (appelé par le frontend React)
    // Exemple d'appel PUT : http://localhost:8081/api/notifications/1/read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            Notification updatedNotification = notificationService.markNotificationAsRead(notificationId);
            return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
        } catch (RuntimeException e) { // Ex: Notification non trouvée
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}