package com.iseem_backend.application.repository;

import com.iseem_backend.application.model.Module;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
@Hidden
public interface ModuleRepository extends JpaRepository<Module , UUID> {
}
