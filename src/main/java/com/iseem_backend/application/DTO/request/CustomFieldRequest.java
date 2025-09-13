package com.iseem_backend.application.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldRequest {
    private String fieldName;
    private String fieldValue;
}
