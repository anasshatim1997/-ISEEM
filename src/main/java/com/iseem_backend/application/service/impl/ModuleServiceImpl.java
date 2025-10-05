package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.ModuleRequest;
import com.iseem_backend.application.DTO.response.ModuleResponse;
import com.iseem_backend.application.exceptions.ModuleAlreadyExistsException;
import com.iseem_backend.application.exceptions.ModuleNotFoundException;
import com.iseem_backend.application.mapper.ModuleMapper;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.Enseignant;
import com.iseem_backend.application.model.Module;
import com.iseem_backend.application.model.Student;
import com.iseem_backend.application.repository.DiplomeRepository;
import com.iseem_backend.application.repository.EnseignantRepository;
import com.iseem_backend.application.repository.ModuleRepository;
import com.iseem_backend.application.repository.StudentRepository;
import com.iseem_backend.application.service.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final EnseignantRepository enseignantRepository;
    private final DiplomeRepository diplomeRepository;
    private final StudentRepository studentRepository;
    private final ModuleMapper moduleMapper;

    @Override
    public ModuleResponse creerModule(ModuleRequest request) {
        if (request.getDiplomeId() != null &&
                moduleRepository.existsByNomAndDiplomeIdDiplome(request.getNom(), request.getDiplomeId())) {
            throw new ModuleAlreadyExistsException("Un module avec ce nom existe déjà pour ce diplôme");
        }

        Enseignant enseignant = null;
        if (request.getEnseignantId() != null) {
            enseignant = enseignantRepository.findById(request.getEnseignantId())
                    .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));
        }

        Diplome diplome = null;
        if (request.getDiplomeId() != null) {
            diplome = diplomeRepository.findById(request.getDiplomeId())
                    .orElseThrow(() -> new RuntimeException("Diplôme introuvable"));
        }

        Module module = moduleMapper.toEntity(request, enseignant, diplome);
        Module savedModule = moduleRepository.save(module);

        log.info("Module créé avec succès: {}", savedModule.getNom());
        return moduleMapper.toResponse(savedModule);
    }

    @Override
    public ModuleResponse modifierModule(UUID id, ModuleRequest request) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException("Module introuvable"));

        if (request.getDiplomeId() != null &&
                !module.getDiplome().getIdDiplome().equals(request.getDiplomeId()) &&
                moduleRepository.existsByNomAndDiplomeIdDiplome(request.getNom(), request.getDiplomeId())) {
            throw new ModuleAlreadyExistsException("Un module avec ce nom existe déjà pour ce diplôme");
        }

        Enseignant enseignant = null;
        if (request.getEnseignantId() != null) {
            enseignant = enseignantRepository.findById(request.getEnseignantId())
                    .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));
        }

        Diplome diplome = null;
        if (request.getDiplomeId() != null) {
            diplome = diplomeRepository.findById(request.getDiplomeId())
                    .orElseThrow(() -> new RuntimeException("Diplôme introuvable"));
        }

        moduleMapper.updateEntity(module, request, enseignant, diplome);
        Module savedModule = moduleRepository.save(module);

        log.info("Module modifié avec succès: {}", savedModule.getNom());
        return moduleMapper.toResponse(savedModule);
    }

    @Override
    public void supprimerModule(UUID id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException("Module introuvable"));

        log.info("Suppression du module: {}", module.getNom());
        moduleRepository.delete(module);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleResponse obtenirModuleParId(UUID id) {
        Module module = moduleRepository.findByIdWithEnseignant(id)
                .orElseThrow(() -> new ModuleNotFoundException("Module introuvable"));

        return moduleMapper.toResponse(module);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> obtenirTousLesModules() {
        List<Module> modules = moduleRepository.findAllWithEnseignant();
        return modules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleResponse> obtenirModulesAvecPagination(Pageable pageable) {
        Page<Module> modules = moduleRepository.findAllWithEnseignant(pageable);
        return modules.map(moduleMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> rechercherModules(String nom) {
        List<Module> modules = moduleRepository.findByNomContainingIgnoreCase(nom);
        return modules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> obtenirModulesParEnseignant(UUID enseignantId) {
        List<Module> modules = moduleRepository.findByEnseignantId(enseignantId);
        return modules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> obtenirModulesParDiplome(UUID diplomeId) {
        List<Module> modules = moduleRepository.findByDiplomeId(diplomeId);
        return modules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ModuleResponse assignerEnseignant(UUID moduleId, UUID enseignantId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException("Module introuvable"));

        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        module.setEnseignant(enseignant);
        Module savedModule = moduleRepository.save(module);

        log.info("Enseignant {} assigné au module {}", enseignant.getUser().getEmail(), module.getNom());
        return moduleMapper.toResponse(savedModule);
    }

    @Override
    public ModuleResponse retirerEnseignant(UUID moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException("Module introuvable"));

        module.setEnseignant(null);
        Module savedModule = moduleRepository.save(module);

        log.info("Enseignant retiré du module {}", module.getNom());
        return moduleMapper.toResponse(savedModule);
    }

    @Override
    public ModuleResponse assignerEtudiant(UUID moduleId, UUID studentId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException("Module introuvable"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        student.getModules().add(module);
        studentRepository.save(student);

        log.info("Étudiant {} assigné au module {}", student.getMatricule(), module.getNom());
        return moduleMapper.toResponse(module);
    }

    @Override
    public ModuleResponse retirerEtudiant(UUID moduleId, UUID studentId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException("Module introuvable"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        student.getModules().remove(module);
        studentRepository.save(student);

        log.info("Étudiant {} retiré du module {}", student.getMatricule(), module.getNom());
        return moduleMapper.toResponse(module);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> obtenirModulesSansEnseignant() {
        List<Module> modules = moduleRepository.findModulesWithoutEnseignant();
        return modules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long compterEtudiantsParModule(UUID moduleId) {
        return moduleRepository.countStudentsByModule(moduleId);
    }
}