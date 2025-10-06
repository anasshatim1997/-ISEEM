package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.response.EnseignantResponse;
import com.iseem_backend.application.enums.StatusEnseignant;
import com.iseem_backend.application.exceptions.DiplomeNotFoundException;
import com.iseem_backend.application.exceptions.EnseignantNotFoundException;
import com.iseem_backend.application.exceptions.ModuleNotFoundException;
import com.iseem_backend.application.exceptions.UserNotFoundException;
import com.iseem_backend.application.mapper.EnseignantMapper;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.Enseignant;
import com.iseem_backend.application.model.Module;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.repository.DiplomeRepository;
import com.iseem_backend.application.repository.EnseignantRepository;
import com.iseem_backend.application.repository.ModuleRepository;
import com.iseem_backend.application.repository.UserRepository;
import com.iseem_backend.application.service.EnseignantService;
import com.iseem_backend.application.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EnseignantServiceImpl implements EnseignantService {

    private final EnseignantRepository enseignantRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final DiplomeRepository diplomeRepository;
    private final EnseignantMapper enseignantMapper;

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public EnseignantResponse ajouter(EnseignantRequest request) {
        UUID userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        Enseignant enseignant = enseignantMapper.toEntity(request, user);

        if (request.getModuleIds() != null) {
            Enseignant finalEnseignant = enseignant;
            Set<Module> modules = request.getModuleIds().stream()
                    .map(moduleId -> {
                        Module m = moduleRepository.findById(moduleId)
                                .orElseThrow(() -> new ModuleNotFoundException(moduleId.toString()));
                        m.setEnseignant(finalEnseignant);
                        return m;
                    })
                    .collect(Collectors.toSet());
            enseignant.setModules(modules);
        }

        if (request.getDiplomeIds() != null) {
            Set<Diplome> diplomes = request.getDiplomeIds().stream()
                    .map(diplomeId -> diplomeRepository.findById(diplomeId)
                            .orElseThrow(() -> new DiplomeNotFoundException(diplomeId.toString())))
                    .collect(Collectors.toSet());
            enseignant.setDiplomes(diplomes);
        }

        enseignant = enseignantRepository.save(enseignant);
        return enseignantMapper.toDto(enseignant);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMINISTRATION','ENSEIGNANT')")
    public EnseignantResponse modifier(UUID id, EnseignantRequest request) {
        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new EnseignantNotFoundException(id));
        enseignantMapper.toEntity(request, enseignant.getUser());
        return enseignantMapper.toDto(enseignantRepository.save(enseignant));
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void supprimer(UUID id) {
        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new EnseignantNotFoundException(id));
        enseignantRepository.delete(enseignant);
    }

    @Override
    @Transactional(readOnly = true)
    public EnseignantResponse obtenirParId(UUID id) {
        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));
        return enseignantMapper.toDto(enseignant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnseignantResponse> obtenirTous() {
        List<Enseignant> enseignants = enseignantRepository.findAll();
        return enseignants.stream()
                .map(enseignantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnseignantResponse> obtenirTousAvecPagination(Pageable pageable) {
        Page<Enseignant> enseignants = enseignantRepository.findAllWithUser(pageable);
        return enseignants.map(enseignantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnseignantResponse> obtenirParSpecialite(String specialite) {
        List<Enseignant> enseignants = enseignantRepository.findBySpecialiteContainingIgnoreCase(specialite);
        return enseignants.stream()
                .map(enseignantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnseignantResponse> obtenirParStatut(String statut) {
        StatusEnseignant status = StatusEnseignant.valueOf(statut.toUpperCase());
        List<Enseignant> enseignants = enseignantRepository.findByStatusEnseignant(status);
        return enseignants.stream()
                .map(enseignantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EnseignantResponse assignerDiplome(UUID enseignantId, UUID diplomeId) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        Diplome diplome = diplomeRepository.findById(diplomeId)
                .orElseThrow(() -> new RuntimeException("Diplôme introuvable"));

        if (diplome.getProfesseurs().contains(enseignant)) {
            throw new RuntimeException("Diplôme déjà assigné à cet enseignant");
        }

        diplome.getProfesseurs().add(enseignant);
        diplomeRepository.save(diplome);

        return enseignantMapper.toDto(enseignant);
    }

    @Override
    public EnseignantResponse assignerModule(UUID enseignantId, UUID moduleId) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        if (module.getEnseignant() != null) {
            throw new RuntimeException("Module déjà assigné à un autre enseignant");
        }

        module.setEnseignant(enseignant);
        moduleRepository.save(module);

        return enseignantMapper.toDto(enseignant);
    }

    @Override
    public EnseignantResponse retirerDiplome(UUID enseignantId, UUID diplomeId) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        Diplome diplome = diplomeRepository.findById(diplomeId)
                .orElseThrow(() -> new RuntimeException("Diplôme introuvable"));

        diplome.getProfesseurs().remove(enseignant);
        diplomeRepository.save(diplome);

        return enseignantMapper.toDto(enseignant);
    }

    @Override
    public EnseignantResponse retirerModule(UUID enseignantId, UUID moduleId) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        if (!module.getEnseignant().equals(enseignant)) {
            throw new RuntimeException("Ce module n'est pas assigné à cet enseignant");
        }

        module.setEnseignant(null);
        moduleRepository.save(module);

        return enseignantMapper.toDto(enseignant);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public List<EnseignantResponse> importerEnseignantsDepuisExcel(MultipartFile file) {
        try {
            log.info("Starting import from Excel file: {}", file.getOriginalFilename());

            List<Enseignant> enseignants = ExcelUtils.importEnseignants(file);

            for (Enseignant enseignant : enseignants) {
                if (userRepository.existsByEmail(enseignant.getUser().getEmail())) {
                    log.warn("Email already exists: {}, skipping", enseignant.getUser().getEmail());
                    continue;
                }

                userRepository.save(enseignant.getUser());
                enseignantRepository.save(enseignant);
            }

            log.info("Import completed successfully. Imported {} enseignants", enseignants.size());

            return enseignants.stream()
                    .map(enseignantMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error importing enseignants from Excel", e);
            throw new RuntimeException("Erreur lors de l'importation: " + e.getMessage(), e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    @Transactional(readOnly = true)
    public byte[] exporterEnseignantsVersExcel() {
        try {
            log.info("Starting export to Excel");

            List<Enseignant> enseignants = enseignantRepository.findAll();
            byte[] excelData = ExcelUtils.exportEnseignants(enseignants);

            log.info("Export completed successfully. Exported {} enseignants", enseignants.size());

            return excelData;
        } catch (IOException e) {
            log.error("Error exporting enseignants to Excel", e);
            throw new RuntimeException("Erreur lors de l'exportation: " + e.getMessage(), e);
        }
    }
}