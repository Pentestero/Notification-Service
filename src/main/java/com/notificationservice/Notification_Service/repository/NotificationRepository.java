// Fichier : notification-service/src/main/java/com/votre_groupe/notificationservice/repository/NotificationRepository.java
package com.votre_groupe.notificationservice.repository;

import com.votre_groupe.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Trouver les notifications non lues pour un utilisateur spécifique
    List<Notification> findByUserIdAndIsReadFalseOrderBySentAtDesc(Long userId);

    // Trouver toutes les notifications pour un utilisateur
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
}