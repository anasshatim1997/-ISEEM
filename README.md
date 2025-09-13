# ISEEM - Backend

ISEEM est une application de gestion académique développée avec **Spring Boot**.  
Elle permet d’administrer les utilisateurs, enseignants, modules et diplômes avec un système sécurisé basé sur les rôles.

---

## 🚀 Fonctionnalités principales

- **Utilisateurs (Users)**
    - Création d’un compte utilisateur par l’administrateur
    - Gestion des rôles (`ADMINISTRATION`, `ENSEIGNANT`, `ETUDIANT`)

- **Enseignants (Professeurs)**
    - Ajout, modification et suppression d’enseignants
    - Attribution des enseignants à des **modules** et des **diplômes**
    - Gestion des informations de travail :
        - Spécialité
        - Date d’embauche
        - Statut (`permanent`, `vacataire`, `contractuel`)
        - Horaires (jour, heure début, heure fin)
        - Champs personnalisés (bureau, expérience, etc.)

- **Modules**
    - Gestion des modules et association avec un enseignant

- **Diplômes**
    - Gestion des diplômes et association avec un ou plusieurs professeurs
    - Génération de diplômes en PDF avec QR code

- **Sécurité**
    - Authentification et autorisation via Spring Security
    - Protection par rôles grâce à `@PreAuthorize`

---

## 🛠️ Technologies utilisées

- Java 17+
- Spring Boot 3 (Web, Data JPA, Security, Validation)
- Hibernate / JPA
- PostgreSQL (ou toute base compatible JPA)
- Lombok
- MapStruct (ou Mapper manuel)
- JWT Authentication
- Maven

---