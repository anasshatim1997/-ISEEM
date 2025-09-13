package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.DemandeAuthentification;
import com.iseem_backend.application.DTO.response.ReponseAuthentification;

public interface ServiceAuthentification {
    ReponseAuthentification authentifier(DemandeAuthentification demande);
}
