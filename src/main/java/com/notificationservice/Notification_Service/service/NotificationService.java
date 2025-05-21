// Fichier : notification-service/src/main/java/com/votre_groupe/notificationservice/service/NotificationService.java
package com.votre_groupe.notificationservice.service;

import com.votre_groupe.notificationservice.model.Notification;
import com.votre_groupe.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Méthode générique pour créer et enregistrer une notification
    @Transactional
    public Notification createNotification(Long userId, String type, String title, String content, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        // sentAt et isRead sont gérés par @PrePersist dans l'entité
        return notificationRepository.save(notification);
    }

    // Méthode spécifique pour la notification de demande de réservation (appelée par le Service de Réservation)
    @Transactional
    public void sendReservationRequestNotificationToOwner(Long ownerUserId, Long requestId) {
        String title = "Nouvelle demande de réservation !";
        String content = "Vous avez reçu une nouvelle demande de réservation (ID: " + requestId + "). Vérifiez votre tableau de bord.";
        createNotification(ownerUserId, "new_reservation_request", title, content, requestId);
        // Ici, vous pourriez aussi intégrer l'envoi de notifications push réelles, d'emails, etc.
    }

    // Méthode spécifique pour la notification d'acceptation de réservation (appelée par le Service de Réservation)
    @Transactional
    public void sendReservationAcceptedNotificationToTenant(Long tenantUserId, Long requestId) {
        String title = "Votre réservation est acceptée !";
        String content = "La demande de réservation (ID: " + requestId + ") a été acceptée par le propriétaire. Préparez-vous !";
        createNotification(tenantUserId, "reservation_accepted", title, content, requestId);
        // Ici, vous pourriez aussi intégrer l'envoi de notifications push réelles, d'emails, etc.
    }

    // Méthode pour récupérer les notifications non lues d'un utilisateur (appelée par le Contrôleur REST)
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderBySentAtDesc(userId);
    }

    // Méthode pour récupérer toutes les notifications d'un utilisateur
    public List<Notification> getAllNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    // Méthode pour marquer une notification comme lue
    @Transactional
    public Notification markNotificationAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId).map(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(OffsetDateTime.now());
            return notificationRepository.save(notification);
        }).orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'ID: " + notificationId));
    }
}