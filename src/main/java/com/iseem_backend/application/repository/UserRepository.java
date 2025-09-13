package com.iseem_backend.application.repository;

import com.iseem_backend.application.enums.Role;
import com.iseem_backend.application.model.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Hidden
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByEmail(String email);
    Set<User> findAllByRole(Role role);
    List<User> findByNomAndPrenomIgnoreCase(String nom, String prenom);
    long countByRole(Role role);
    boolean existsByEmail(String email);
}
