package ru.docapp.documentapp.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateLogEntry {
    private String entityType;
    private String duplicateValue;
    private String context;
}