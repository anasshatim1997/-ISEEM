package com.iseem_backend.application.mapper;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.request.HoraireRequest;
import com.iseem_backend.application.DTO.response.CustomFieldResponse;
import com.iseem_backend.application.DTO.response.EnseignantResponse;
import com.iseem_backend.application.DTO.response.HoraireResponse;
import com.iseem_backend.application.model.CustomField;
import com.iseem_backend.application.model.Enseignant;
import com.iseem_backend.application.model.Module;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.User;
import com.iseem_backend.application.utils.TimeSlot;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EnseignantMapper {

    public Enseignant toEntity(EnseignantRequest request, User user) {
        Enseignant enseignant = new Enseignant();
        enseignant.setUser(user);
        enseignant.setSpecialite(request.getSpecialite());
        enseignant.setDateEmbauche(request.getDateEmbauche());
        enseignant.setStatusEnseignant(request.getStatusEnseignant());
        enseignant.setHeuresTravail(request.getHeuresTravail());

        if (request.getHoraire() != null) {
            HoraireRequest hr = request.getHoraire();
            TimeSlot slot = new TimeSlot();
            slot.setDay(DayOfWeek.valueOf(hr.getJour().toUpperCase()));
            slot.setStartTime(LocalTime.parse(hr.getHeureDebut()));
            slot.setEndTime(LocalTime.parse(hr.getHeureFin()));
            enseignant.setHoraire(slot);
        }

        if (request.getCustomFields() != null) {
            Set<CustomField> fields = request.getCustomFields().stream()
                    .map(cf -> {
                        CustomField f = new CustomField();
                        f.setFieldName(cf.getFieldName());
                        f.setFieldValue(cf.getFieldValue());
                        f.setEnseignant(enseignant);
                        return f;
                    }).collect(Collectors.toSet());
            enseignant.setCustomFields(fields);
        }

        return enseignant;
    }

    public EnseignantResponse toDto(Enseignant enseignant) {
        EnseignantResponse response = new EnseignantResponse();
        response.setEnseignantId(enseignant.getEnseignantId());
        response.setUserId(enseignant.getUser() != null ? enseignant.getUser().getUserId() : null);
        response.setSpecialite(enseignant.getSpecialite());
        response.setDateEmbauche(enseignant.getDateEmbauche());
        response.setStatusEnseignant(enseignant.getStatusEnseignant());
        response.setHeuresTravail(enseignant.getHeuresTravail());

        if (enseignant.getHoraire() != null) {
            TimeSlot slot = enseignant.getHoraire();
            HoraireResponse hr = new HoraireResponse();
            hr.setJour(slot.getDay().name());
            hr.setHeureDebut(slot.getStartTime().toString());
            hr.setHeureFin(slot.getEndTime().toString());
            response.setHoraire(hr);
        }

        if (enseignant.getCustomFields() != null) {
            Set<CustomFieldResponse> cf = enseignant.getCustomFields().stream()
                    .map(f -> {
                        CustomFieldResponse r = new CustomFieldResponse();
                        r.setFieldName(f.getFieldName());
                        r.setFieldValue(f.getFieldValue());
                        return r;
                    }).collect(Collectors.toSet());
            response.setCustomFields(cf);
        }

        if (enseignant.getModules() != null) {
            Set<UUID> moduleIds = enseignant.getModules().stream()
                    .map(Module::getIdModule)
                    .collect(Collectors.toSet());
            response.setModuleIds(moduleIds);
        }

        if (enseignant.getDiplomes() != null) {
            Set<UUID> diplomeIds = enseignant.getDiplomes().stream()
                    .map(Diplome::getIdDiplome)
                    .collect(Collectors.toSet());
            response.setDiplomeIds(diplomeIds);
        }

        return response;
    }
}