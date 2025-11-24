package ru.docapp.documentapp.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateLogEntry {
    private String entityType;      // e.g., "DOCUMENT"
    private String duplicateValue;  // e.g., "DOC-2025-001"
    private String context;         // e.g., "Attempt to create document with existing number"
}