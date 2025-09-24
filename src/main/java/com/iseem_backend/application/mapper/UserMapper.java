package com.iseem_backend.application.mapper;

import com.iseem_backend.application.DTO.request.UserRequest;
import com.iseem_backend.application.DTO.request.UserUpdateRequest;
import com.iseem_backend.application.DTO.response.UserResponse;
import com.iseem_backend.application.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest userRequest) {
        return User.builder()
                .email(userRequest.getEmail())
                .passwordHash(userRequest.getPassword())
                .role(userRequest.getRole())
                .nom(userRequest.getNom())
                .prenom(userRequest.getPrenom())
                .telephone(userRequest.getTelephone())
                .image(userRequest.getImage())
                .build();
    }

    public UserResponse toDto(User user) {
        return UserResponse.builder()
                .idUser(user.getUserId())
                .email(user.getEmail())
                .image(user.getImage())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .build();
    }


   public void updateUserFromDto(UserUpdateRequest request, User user) {
    if (request == null || user == null) return;

    if (request.getEmail() != null) {
        user.setEmail(request.getEmail());
    }
    if (request.getNom() != null) {
        user.setNom(request.getNom());
    }
    if (request.getPrenom() != null) {
        user.setPrenom(request.getPrenom());
    }
    if (request.getTelephone() != null) {
        user.setTelephone(request.getTelephone());
    }
    if (request.getImage() != null) {
        user.setImage(request.getImage());
    }
}

}

