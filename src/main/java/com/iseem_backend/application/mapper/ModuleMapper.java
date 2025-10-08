package com.iseem_backend.application.mapper;

import com.iseem_backend.application.DTO.request.ModuleRequest;
import com.iseem_backend.application.DTO.response.ModuleResponse;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.Enseignant;
import com.iseem_backend.application.model.Module;
import com.iseem_backend.application.model.Student;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ModuleMapper {

    public Module toEntity(ModuleRequest request, Enseignant enseignant, Diplome diplome) {
        return Module.builder()
                .nom(request.getNom())
                .coefficient(request.getCoefficient())
                .description(request.getDescription())
                .heuresTotal(request.getHeuresTotal())
                .heuresCours(request.getHeuresCours())
                .heuresTD(request.getHeuresTD())
                .heuresTP(request.getHeuresTP())
                .enseignant(enseignant)
                .diplome(diplome)
                .build();
    }

    public ModuleResponse toResponse(Module module) {
        return ModuleResponse.builder()
                .idModule(module.getIdModule())
                .nom(module.getNom())
                .coefficient(module.getCoefficient())
                .description(module.getDescription())
                .heuresTotal(module.getHeuresTotal())
                .heuresCours(module.getHeuresCours())
                .heuresTD(module.getHeuresTD())
                .heuresTP(module.getHeuresTP())
                .note(module.getNote())
                .enseignantId(module.getEnseignant() != null ? module.getEnseignant().getEnseignantId() : null)
                .enseignantNom(module.getEnseignant() != null ? module.getEnseignant().getUser().getNom() : null)
                .enseignantPrenom(module.getEnseignant() != null ? module.getEnseignant().getUser().getPrenom() : null)
                .diplomeId(module.getDiplome() != null ? module.getDiplome().getIdDiplome() : null)
                .diplomeNom(module.getDiplome() != null ? module.getDiplome().getNomDiplome() : null)
                .studentIds(module.getStudents() != null ?
                        module.getStudents().stream()
                                .map(Student::getUserId)
                                .collect(Collectors.toSet()) : null)
                .nombreEtudiants(module.getStudents() != null ? module.getStudents().size() : 0)
                .build();
    }

    public void updateEntity(Module module, ModuleRequest request, Enseignant enseignant, Diplome diplome) {
        module.setNom(request.getNom());
        module.setCoefficient(request.getCoefficient());
        module.setDescription(request.getDescription());
        module.setHeuresTotal(request.getHeuresTotal());
        module.setHeuresCours(request.getHeuresCours());
        module.setHeuresTD(request.getHeuresTD());
        module.setHeuresTP(request.getHeuresTP());
        module.setEnseignant(enseignant);
        module.setDiplome(diplome);
    }
}