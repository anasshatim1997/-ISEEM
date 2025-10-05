package com.iseem_backend.application.repository;

import com.iseem_backend.application.model.Module;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
@Hidden
public interface ModuleRepository extends JpaRepository<Module, UUID> {

    List<Module> findByNomContainingIgnoreCase(String nom);

    List<Module> findByEnseignantEnseignantId(UUID enseignantId);

    List<Module> findByDiplomeIdDiplome(UUID diplomeId);

    @Query("SELECT m FROM Module m JOIN FETCH m.enseignant e JOIN FETCH e.user WHERE m.idModule = :id")
    Optional<Module> findByIdWithEnseignant(@Param("id") UUID id);

    @Query("SELECT m FROM Module m JOIN FETCH m.enseignant e JOIN FETCH e.user")
    List<Module> findAllWithEnseignant();

    @Query("SELECT m FROM Module m JOIN FETCH m.enseignant e JOIN FETCH e.user")
    Page<Module> findAllWithEnseignant(Pageable pageable);

    @Query("SELECT m FROM Module m WHERE m.enseignant.enseignantId = :enseignantId")
    List<Module> findByEnseignantId(@Param("enseignantId") UUID enseignantId);

    @Query("SELECT m FROM Module m WHERE m.diplome.idDiplome = :diplomeId")
    List<Module> findByDiplomeId(@Param("diplomeId") UUID diplomeId);

    boolean existsByNomAndDiplomeIdDiplome(String nom, UUID diplomeId);

    @Query("SELECT COUNT(s) FROM Module m JOIN m.students s WHERE m.idModule = :moduleId")
    long countStudentsByModule(@Param("moduleId") UUID moduleId);

    @Query("SELECT m FROM Module m WHERE m.enseignant IS NULL")
    List<Module> findModulesWithoutEnseignant();
}