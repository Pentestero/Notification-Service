// Fichier : notification-service/src/main/java/com/votre_groupe/notificationservice/model/Notification.java
package com.votre_groupe.notificationservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications") // Mappe à la table 'notifications'
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // L'ID de l'utilisateur destinataire

    @Column(nullable = false)
    private String type; // Type de notification (ex: 'new_reservation_request', 'reservation_accepted')

    @Column
    private String title; // Titre de la notification

    @Column(nullable = false)
    private String content; // Contenu du message de notification

    @Column(name = "related_id")
    private Long relatedId; // ID d'une entité liée (ex: ID de la demande de réservation)

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // Statut de lecture (par défaut non lu)

    @Column(name = "sent_at", nullable = false)
    private OffsetDateTime sentAt; // Horodatage d'envoi

    @Column(name = "read_at")
    private OffsetDateTime readAt; // Horodatage de lecture (NULL si non lu)

    @PrePersist
    protected void onCreate() {
        this.sentAt = OffsetDateTime.now();
        this.isRead = false; // S'assurer qu'il est non lu par défaut
    }
}