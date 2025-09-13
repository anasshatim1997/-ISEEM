package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.DiplomeRequest;
import com.iseem_backend.application.DTO.response.DiplomeResponse;
import com.iseem_backend.application.enums.TypeDiplome;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DiplomeService {

    DiplomeResponse creerDiplome(DiplomeRequest request);

    DiplomeResponse modifierDiplome(UUID idDiplome, DiplomeRequest request);

    void validerDiplome(UUID idDiplome);

    void signerDiplome(UUID idDiplome, UUID adminId);

    void supprimerDiplome(UUID idDiplome);

    DiplomeResponse obtenirDiplomeParId(UUID idDiplome);

    List<DiplomeResponse> obtenirTousDiplomes();

    List<DiplomeResponse> filtrerDiplomesParType(TypeDiplome typeDiplome);

    byte[] exporterDiplomes();

    void importerDiplomes(MultipartFile file);

    byte[] genererQRCode(UUID idDiplome);

    byte[] genererPDF(UUID idDiplome);
}
