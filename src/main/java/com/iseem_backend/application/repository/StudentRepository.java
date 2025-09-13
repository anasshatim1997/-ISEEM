package com.iseem_backend.application.repository;

import com.iseem_backend.application.model.Student;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Hidden
public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByMatricule(String matricule);

    boolean existsByMatricule(String matricule);

    @Query("SELECT s FROM Student s JOIN s.user u WHERE " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :prenom, '%')) AND " +
            "LOWER(s.matricule) LIKE LOWER(CONCAT('%', :matricule, '%'))")
    List<Student> findByUserNomContainingIgnoreCaseAndUserPrenomContainingIgnoreCaseAndMatriculeContainingIgnoreCase(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("matricule") String matricule);
}