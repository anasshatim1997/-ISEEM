package com.iseem_backend.application.mapper;

import com.iseem_backend.application.DTO.request.FormationRequest;
import com.iseem_backend.application.DTO.response.FormationResponse;
import com.iseem_backend.application.model.Enseignant;
import com.iseem_backend.application.model.Formation;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class FormationMapper {

    public Formation toEntity(FormationRequest request) {
        if (request == null) return null;

        return Formation.builder()
                .nom(request.getNom())
                .duree(request.getDuree())
                .cout(request.getCout())
                .description(request.getDescription())
                .anneeFormation(request.getAnneeFormation())
                .estActive(request.getEstActive())
                .modeFormation(request.getModeFormation())
                .niveauAcces(request.getNiveauAcces())
                .capaciteMax(request.getCapaciteMax())
                .build();
    }

    public FormationResponse toDto(Formation formation) {
        if (formation == null) return null;

        return FormationResponse.builder()
                .idFormation(formation.getIdFormation())
                .nom(formation.getNom())
                .duree(formation.getDuree())
                .cout(formation.getCout())
                .description(formation.getDescription())
                .anneeFormation(formation.getAnneeFormation())
                .estActive(formation.getEstActive())
                .modeFormation(formation.getModeFormation())
                .niveauAcces(formation.getNiveauAcces())
                .capaciteMax(formation.getCapaciteMax())
                .enseignantsIds(formation.getEnseignants() != null ?
                        formation.getEnseignants().stream().map(Enseignant::getEnseignantId).collect(Collectors.toSet())
                        : null)
                .emploiDuTempsId(formation.getEmploiDuTemps() != null ? formation.getEmploiDuTemps().getId() : null)
                .build();
    }

    public void updateEntityFromRequest(FormationRequest request, Formation formation) {
        if (request == null || formation == null) return;

        formation.setNom(request.getNom());
        formation.setDuree(request.getDuree());
        formation.setCout(request.getCout());
        formation.setDescription(request.getDescription());
        formation.setAnneeFormation(request.getAnneeFormation());
        formation.setEstActive(request.getEstActive());
        formation.setModeFormation(request.getModeFormation());
        formation.setNiveauAcces(request.getNiveauAcces());
        formation.setCapaciteMax(request.getCapaciteMax());
    }
}
