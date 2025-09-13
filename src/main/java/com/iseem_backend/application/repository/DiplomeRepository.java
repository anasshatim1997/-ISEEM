package com.iseem_backend.application.repository;

import com.iseem_backend.application.enums.TypeDiplome;
import com.iseem_backend.application.model.Diplome;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
@Hidden
public interface DiplomeRepository extends JpaRepository<Diplome, UUID> {
    List<Diplome> findByTypeDiplome(TypeDiplome typeDiplome);

}
