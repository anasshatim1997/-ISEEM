package com.iseem_backend.application.mapper;

import com.iseem_backend.application.DTO.request.UserRequest;
import com.iseem_backend.application.DTO.response.UserResponse;
import com.iseem_backend.application.DTO.response.UserUpdateRequest;
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

        User.builder()
                .userId(user.getUserId())
                .email(request.getEmail())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .telephone(request.getTelephone())
                .image(request.getImage())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

}

