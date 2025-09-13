package com.iseem_backend.application.DTO.response;


import lombok.Data;

@Data
public class HoraireResponse {
    private String jour;
    private String heureDebut;
    private String heureFin;
}