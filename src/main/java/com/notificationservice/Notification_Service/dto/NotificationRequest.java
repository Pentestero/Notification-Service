// Fichier : notification-service/src/main/java/com/votre_groupe/notificationservice/dto/NotificationRequest.java
package com.votre_groupe.notificationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Long relatedId;
}