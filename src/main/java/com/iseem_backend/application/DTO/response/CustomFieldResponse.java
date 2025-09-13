package com.iseem_backend.application.DTO.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldResponse {
    private UUID id;
    private String fieldName;
    private String fieldValue;
}
