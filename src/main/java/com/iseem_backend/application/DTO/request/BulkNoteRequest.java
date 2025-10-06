package com.iseem_backend.application.DTO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkNoteRequest {
    @NotNull
    private UUID enseignantId;

    @NotEmpty
    @Valid
    private List<NoteRequest> notes;
}