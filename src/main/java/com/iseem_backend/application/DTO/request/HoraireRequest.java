package com.iseem_backend.application.DTO.request;

import lombok.Data;

@Data
public class HoraireRequest {
    private String jour;
    private String heureDebut;
    private String heureFin;
}
