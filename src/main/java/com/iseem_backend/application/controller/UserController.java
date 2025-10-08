package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.DemandeAuthentification;
import com.iseem_backend.application.DTO.response.ReponseAuthentification;
import com.iseem_backend.application.DTO.response.UserUpdateRequest;
import com.iseem_backend.application.DTO.request.UserRequest;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.service.ServiceAuthentification;
import com.iseem_backend.application.service.UserService;
import com.iseem_backend.application.utils.handler.GlobalResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Gestion des utilisateurs", description = "Endpoints pour gérer les utilisateurs, étudiants et enseignants")
public class UserController {

    private final ServiceAuthentification serviceAuthentification;
    private final UserService userService;

    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un jeton JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody DemandeAuthentification demande) {
        ReponseAuthentification reponse = serviceAuthentification.authentifier(demande);
        return GlobalResponseHandler.success(reponse, "Authentification réussie");
    }

    @Operation(summary = "Créer un nouvel utilisateur", description = "Créer un nouvel utilisateur (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/admin/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest) {
        User newUser = userService.creerUtilisateur(userRequest);
        return GlobalResponseHandler.success(newUser, "Utilisateur créé avec succès");
    }

    @Operation(summary = "Lister tous les étudiants", description = "Récupère tous les étudiants (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiants trouvés avec succès")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin/users/students")
    public ResponseEntity<?> findAllStudents() {
        return GlobalResponseHandler.success(userService.findAllStudents(), "Étudiants trouvés avec succès");
    }

    @Operation(summary = "Lister tous les enseignants", description = "Récupère tous les enseignants (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enseignants trouvés avec succès")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin/users/teachers")
    public ResponseEntity<?> findAllTeachers() {
        return GlobalResponseHandler.success(userService.findAllTeachers(), "Enseignants trouvés avec succès");
    }

    @Operation(summary = "Récupérer un utilisateur par ID", description = "Récupère un utilisateur par son UUID (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin/user/{id}")
    public ResponseEntity<?> findUserById(
            @Parameter(description = "UUID de l'utilisateur à récupérer") @PathVariable UUID id) {
        return GlobalResponseHandler.success(userService.findUserById(id), "Utilisateur trouvé avec succès");
    }

    @Operation(summary = "Récupérer un utilisateur par email", description = "Récupère un utilisateur par son email (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin/user/by-email")
    public ResponseEntity<?> findUserByEmail(
            @Parameter(description = "Email de l'utilisateur") @RequestParam String email) {
        return GlobalResponseHandler.success(userService.findUserByEmail(email), "Utilisateur trouvé avec succès");
    }

    @Operation(summary = "Récupérer des utilisateurs par nom et prénom", description = "Récupère des utilisateurs par leur prénom et nom (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateurs trouvés avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé avec ces informations")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/admin/user/username")
    public ResponseEntity<?> getUsername(
            @Parameter(description = "Prénom de l'utilisateur") @RequestParam String firstName,
            @Parameter(description = "Nom de l'utilisateur") @RequestParam String lastName) {
        return GlobalResponseHandler.success(userService.findUsersByUsername(firstName, lastName), "Utilisateurs récupérés avec succès");
    }

    @Operation(summary = "Mettre à jour un utilisateur", description = "Met à jour les informations d'un utilisateur par UUID (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/admin/user/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "UUID de l'utilisateur à mettre à jour") @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return GlobalResponseHandler.success(
                userService.updateUser(userUpdateRequest, id),
                "Utilisateur mis à jour avec succès"
        );
    }

    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur par UUID (Admin uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/admin/user/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "UUID de l'utilisateur à supprimer") @PathVariable UUID id) {
        userService.deleteUser(id);
        return GlobalResponseHandler.success(null, "Utilisateur supprimé avec succès");
    }
}
