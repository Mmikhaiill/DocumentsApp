package ru.docapp.documentapp.controllers;

import ru.docapp.documentapp.dto.DocumentDto;
import ru.docapp.documentapp.entities.Document;
import ru.docapp.documentapp.services.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public List<Document> getAll() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentWithSpecifications(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Document> create(@Valid @RequestBody DocumentDto dto) {
        Document doc = documentService.createDocument(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> update(@PathVariable Long id, @Valid @RequestBody DocumentDto dto) {
        Document doc = documentService.updateDocument(id, dto);
        return ResponseEntity.ok(doc);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        documentService.deleteDocument(id);
    }
}