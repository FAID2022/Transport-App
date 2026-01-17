# 🚌 Système de Monitoring de Transport Scolaire

Une solution de suivi de transport scolaire en temps réel développée avec **Spring Boot**, **Apache Kafka** et **MySQL**. Ce projet permet de suivre la position des bus, de détecter les sorties de zone (Geofencing) et de gérer les pénalités pour un réseau de transport scolaire.

<img width="1365" height="598" alt="image" src="https://github.com/user-attachments/assets/8fe3c944-b72b-4d67-b3a9-22dc157dcb2d" />



## 🚀 Fonctionnalités

* **Suivi Temps Réel :** Ingestion de données GPS à haute fréquence via Apache Kafka.
* **Geofencing :** Détection automatique des bus sortant des zones autorisées.
* **Système de Pénalités :** Enregistrement automatique des infractions (retards, déviation de route) dans MySQL.
* **Tableau de Bord Interactif :** Interface web pour visualiser la position des bus et gérer les familles.

## 🛠️ Stack Technique

* **Backend :** Java 17+, Spring Boot (Web, Data JPA, Kafka)
* **Messaging :** Apache Kafka, Zookeeper
* **Base de Données :** MySQL
* **Frontend :** HTML5, JavaScript (API Leaflet pour les cartes)
* **Outil de Build :** Maven

## ⚙️ Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :
1.  **Java Development Kit (JDK)** (version 17 ou supérieure)
2.  **Apache Kafka** (Version binaire)
3.  **Serveur MySQL**

## 🏃‍♂️ Guide d'Installation et de Démarrage

### 1. Configuration de la Base de Données
1.  Ouvrez votre client MySQL (Workbench ou ligne de commande).
2.  Créez la base de données :
    ```sql
    CREATE DATABASE transport_scolaire;
    ```
3.  Configurez vos identifiants dans `src/main/resources/application.properties` :
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/transport_scolaire
    spring.datasource.username=VOTRE_NOM_UTILISATEUR
    spring.datasource.password=VOTRE_MOT_DE_PASSE
    ```

### 2. Démarrage d'Apache Kafka (Windows)
Allez dans votre dossier d'installation Kafka (ex: `C:\kafka`) et lancez les exécutables comme indiqué sur les captures ci-dessous :

**Terminal 1 - Démarrage de Zookeeper :**
<img width="1349" height="169" alt="image" src="https://github.com/user-attachments/assets/9ed6b779-bf19-4b3d-9f36-743d24f26334" />


**Terminal 2 - Démarrage du Serveur Kafka :**
<img width="1348" height="267" alt="image" src="https://github.com/user-attachments/assets/96dfdd3e-5766-4326-9066-87c7488017f4" />


*Note : Le topic `bus-positions` sera créé automatiquement par l'application Spring Boot au démarrage.*

### 3. Lancement de l'Application
1.  Clonez le dépôt :
    ```bash
    git clone https://github.com/MOOUUAAD/KAFKA-transport.git
    ```
2.  Ouvrez le projet dans IntelliJ IDEA.
3.  Exécutez la classe `SchoolTransportBackendApplication`.
4.  Exécutez la classe `BusSimulation`.

### 4. Accès au Tableau de Bord
Une fois le backend lancé, ouvrez votre navigateur et allez sur :
http://localhost:8080/index.html

## 📂 Structure du Projet

```text
src/main/java/com/ecole/transport
├── config/          # Configuration du Topic Kafka
├── controller/      # Endpoints REST (Bus, Penalty, Admin)
├── model/           # Entités JPA (Parent, Penalty)
├── repository/      # Repositories Spring Data
├── service/         # Logique Métier (Geofencing, Kafka Consumer)
├── simulation/      # Class de Simulation de Bus
└── SchoolTransportBackendApplication         # Main Class

