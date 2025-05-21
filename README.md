# Service de Notification (notification-service)

Ce microservice Spring Boot est responsable de la gestion et de l'envoi des notifications aux utilisateurs (Proprietaires et locataires) de la plateforme. Il permet d'enregistrer des notifications dans la base de données et de les rendre accessibles via des APIs REST.

## 🚀 Démarrage Rapide

Pour lancer ce service localement, suivez les étapes ci-dessous.

### Prérequis
* Java Development Kit (JDK) 17 ou 21
* Maven (installé et configuré)
* Docker Desktop (Windows) ou Docker Engine + Docker Compose V2 (Linux)
* Accès à la base de données PostgreSQL partagée (voir section "Base de Données")

### 1. Configuration de la Base de Données

Ce service se connecte à une base de données PostgreSQL partagée. Assurez-vous que la base de données est lancée et que son schéma est appliqué.

1.  **Lancer la Base de Données :**
    * Naviguez vers le dossier contenant `docker-compose.yml` et `schema.sql` (ces fichiers devraient vous être fournis par l'équipe d'infrastructure ou le chef de projet).
    * Exécutez :
        ```bash
        docker compose up -d
        ```
    * *(Vérifiez avec `docker compose ps` que le conteneur `my_microservices_postgres_db` est `Up`.)*

2.  **Appliquer le Schéma de la Base de Données :**
    * Exécutez (en remplaçant `VOTRE_MOT_DE_PASSE_SECURISE` par le vrai mot de passe de la DB) :
        ```bash
        docker compose exec db psql -U user_app -d microservices_db < schema.sql
        ```
    * *(Entrez le mot de passe lorsque demandé.)*

### 2. Configuration du Service

1.  **Cloner le Dépôt :**
    ```bash
    git clone [https://github.com/VotreNomUtilisateur/notification-service.git](https://github.com/VotreNomUtilisateur/notification-service.git)
    cd notification-service
    ```
2.  **Mettre à jour `application.properties` :**
    * Ouvrez le fichier `src/main/resources/application.properties`.
    * Assurez-vous que les informations de connexion à la base de données correspondent à votre configuration locale (utilisateur, mot de passe, nom de la DB).
    * **Port du Service :** Ce service écoute par défaut sur le port `8081`. Assurez-vous qu'aucun autre service ne l'utilise sur votre machine.
        ```properties
        server.port=8081
        spring.datasource.url=jdbc:postgresql://localhost:5432/microservices_db
        spring.datasource.username=user_app
        spring.datasource.password=VOTRE_MOT_DE_PASSE_SECURISE # <--- VOTRE MOT DE PASSE !
        spring.datasource.driver-class-name=org.postgresql.Driver
        spring.jpa.hibernate.ddl-auto=none
        spring.jpa.show-sql=true
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
        ```

### 3. Lancer le Service

* Dans le dossier racine du service (`notification-service`), exécutez :
    ```bash
    mvn spring-boot:run
    ```
* Le service devrait démarrer et être accessible sur `http://localhost:8081`.

## 📚 Documentation des APIs (Endpoints)

Ce service expose les APIs REST suivantes pour la gestion des notifications.

**Base URL :** `http://localhost:8081/api/notifications`

### 1. Envoyer une Notification Générique (POST)
* **Endpoint :** `/send`
* **Méthode :** `POST`
* **Description :** Permet à tout service d'envoyer une notification à un utilisateur.
* **Corps de la Requête (JSON) :**
    ```json
    {
      "userId": 1,         // ID de l'utilisateur destinataire (obligatoire)
      "type": "new_message", // Type de notification (ex: "new_message", "payment_success")
      "title": "Nouveau message !", // Titre de la notification (optionnel)
      "content": "Vous avez reçu un nouveau message de John Doe.", // Contenu détaillé (obligatoire)
      "relatedId": 123     // ID d'une entité liée (ex: message_id, request_id) (optionnel)
    }
    ```
* **Réponse Succès :** `201 Created` avec l'objet Notification créé.

### 2. Envoyer une Notification de Demande de Réservation (POST)
* **Endpoint :** `/send/reservation-request`
* **Méthode :** `POST`
* **Description :** Envoie une notification au propriétaire d'une chambre concernant une nouvelle demande de réservation.
* **Paramètres de Requête (Query Parameters) :**
    * `ownerUserId` (Long) : L'ID de l'utilisateur propriétaire de la chambre.
    * `requestId` (Long) : L'ID de la demande de réservation.
* **Exemple d'Appel :** `http://localhost:8081/api/notifications/send/reservation-request?ownerUserId=101&requestId=500`
* **Réponse Succès :** `200 OK` (sans corps).

### 3. Envoyer une Notification d'Acceptation de Réservation (POST)
* **Endpoint :** `/send/reservation-accepted`
* **Méthode :** `POST`
* **Description :** Envoie une notification au locataire lorsque sa demande de réservation a été acceptée.
* **Paramètres de Requête (Query Parameters) :**
    * `tenantUserId` (Long) : L'ID de l'utilisateur locataire.
    * `requestId` (Long) : L'ID de la demande de réservation.
* **Exemple d'Appel :** `http://localhost:8081/api/notifications/send/reservation-accepted?tenantUserId=102&requestId=500`
* **Réponse Succès :** `200 OK` (sans corps).

### 4. Récupérer les Notifications Non Lues d'un Utilisateur (GET)
* **Endpoint :** `/unread/{userId}`
* **Méthode :** `GET`
* **Description :** Récupère la liste de toutes les notifications non lues pour un utilisateur donné.
* **Paramètre de Chemin (Path Parameter) :**
    * `userId` (Long) : L'ID de l'utilisateur dont on veut les notifications.
* **Exemple d'Appel :** `http://localhost:8081/api/notifications/unread/1`
* **Réponse Succès :** `200 OK` avec une liste d'objets Notification (JSON). `204 No Content` si aucune notification non lue.

### 5. Récupérer Toutes les Notifications d'un Utilisateur (GET)
* **Endpoint :** `/all/{userId}`
* **Méthode :** `GET`
* **Description :** Récupère la liste de toutes les notifications (lues et non lues) pour un utilisateur donné.
* **Paramètre de Chemin (Path Parameter) :**
    * `userId` (Long) : L'ID de l'utilisateur.
* **Exemple d'Appel :** `http://localhost:8081/api/notifications/all/1`
* **Réponse Succès :** `200 OK` avec une liste d'objets Notification (JSON). `204 No Content` si aucune notification.

### 6. Marquer une Notification comme Lue (PUT)
* **Endpoint :** `/{notificationId}/read`
* **Méthode :** `PUT`
* **Description :** Marque une notification spécifique comme lue.
* **Paramètre de Chemin (Path Parameter) :**
    * `notificationId` (Long) : L'ID de la notification à marquer comme lue.
* **Exemple d'Appel :** `http://localhost:8081/api/notifications/123/read`
* **Réponse Succès :** `200 OK` avec l'objet Notification mis à jour. `404 Not Found` si la notification n'existe pas.

## 🤝 Contribution

Pour toute question ou contribution, veuillez contacter les renseignez ici...!