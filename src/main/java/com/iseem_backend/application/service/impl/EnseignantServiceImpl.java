package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.response.EnseignantResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public EnseignantResponse assignerDiplome(UUID enseignantId, UUID diplomeId) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new EnseignantNotFoundException(enseignantId));
        Diplome diplome = diplomeRepository.findById(diplomeId)
                .orElseThrow(() -> new DiplomeNotFoundException(diplomeId));
        enseignant.getDiplomes().add(diplome);
        diplome.getProfesseurs().add(enseignant);
        diplomeRepository.save(diplome);
        return enseignantMapper.toDto(enseignantRepository.save(enseignant));
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public EnseignantResponse assignerModule(UUID enseignantId, UUID moduleId) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new EnseignantNotFoundException(enseignantId));
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException(moduleId));
        module.setEnseignant(enseignant);
        moduleRepository.save(module);
        return enseignantMapper.toDto(enseignant);
    }
}
