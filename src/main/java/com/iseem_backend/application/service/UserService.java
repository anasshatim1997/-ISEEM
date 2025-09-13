package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.UserRequest;
import com.iseem_backend.application.DTO.response.UserResponse;
import com.iseem_backend.application.DTO.response.UserUpdateRequest;
import com.iseem_backend.application.model.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    User creerUtilisateur(UserRequest userRequest);
    Set<UserResponse> findAllStudents();
    Set<UserResponse> findAllTeachers();
    UserResponse findUserById(UUID id);
    UserResponse findUserByEmail(String email);
    List<UserResponse> findUsersByUsername(String firstName, String lastName);
    UserResponse updateUser(UserUpdateRequest userUpdateRequest , UUID idUser);
    void deleteUser(UUID id);
}
