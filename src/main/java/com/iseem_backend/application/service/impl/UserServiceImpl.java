package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.UserRequest;
import com.iseem_backend.application.DTO.request.UserUpdateRequest;
import com.iseem_backend.application.DTO.response.UserResponse;
import com.iseem_backend.application.enums.Role;
import com.iseem_backend.application.exceptions.UserAlreadyExistsException;
import com.iseem_backend.application.exceptions.UserNotFoundException;
import com.iseem_backend.application.exceptions.NoUsersFoundException;
import com.iseem_backend.application.mapper.UserMapper;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.repository.UserRepository;
import com.iseem_backend.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encodeurMotDePasse;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public User creerUtilisateur(UserRequest userRequest) {
        userRepository.findUserByEmail(userRequest.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("Un utilisateur avec l'email " + user.getEmail() + " existe déjà");
                });
        User newUser = userMapper.toEntity(userRequest);
        newUser.setPasswordHash(encodeurMotDePasse.encode(userRequest.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public Set<UserResponse> findAllStudents() {
        return userRepository.findAllByRole(Role.ETUDIANT).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public Set<UserResponse> findAllTeachers() {
        return userRepository.findAllByRole(Role.ENSEIGNANT).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public UserResponse findUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public UserResponse findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public List<UserResponse> findUsersByUsername(String firstName, String lastName) {
        List<User> users = userRepository.findByNomAndPrenomIgnoreCase(firstName, lastName);
        if (users.isEmpty()) {
            throw new NoUsersFoundException("Aucun utilisateur trouvé avec le nom : " + firstName + " " + lastName);
        }
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest, UUID idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'id: " + idUser));
        userMapper.updateUserFromDto(userUpdateRequest, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'id: " + id));
        userRepository.delete(user);
    }
}
