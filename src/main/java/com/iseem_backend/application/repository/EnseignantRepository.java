package com.iseem_backend.application.repository;

import com.iseem_backend.application.enums.StatusEnseignant;
import com.iseem_backend.application.model.Enseignant;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


@Repository
@Transactional
@Hidden
public interface EnseignantRepository extends JpaRepository<Enseignant , UUID> {
    List<Enseignant> findBySpecialiteContainingIgnoreCase(String specialite);

    List<Enseignant> findByStatusEnseignant(StatusEnseignant status);


    @Query("SELECT e FROM Enseignant e JOIN FETCH e.user")
    Page<Enseignant> findAllWithUser(Pageable pageable);
}
