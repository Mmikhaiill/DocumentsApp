package ru.docapp.documentapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.docapp.documentapp.dto.DocumentDto;
import ru.docapp.documentapp.dto.DocumentResponseDto;
import ru.docapp.documentapp.dto.SpecificationResponseDto;
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
@Tag(name = "Документы", description = "Операции с документами и спецификациями")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "Получить все документы", description = "Возвращает список всех документов с их спецификациями.")
    @GetMapping
    public List<DocumentResponseDto> getAll() {
        return documentService.getAllDocuments().stream()
                .map(this::toResponseDto)
                .toList();
    }



    @Operation(summary = "Получить документ по ID", description = "Возвращает документ с полной информацией о спецификациях.")
    @ApiResponse(responseCode = "200", description = "Документ найден")
    @ApiResponse(responseCode = "404", description = "Документ не найден")
    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentWithSpecifications(id));
    }


    @Operation(summary = "Создать новый документ", description = "Создаёт документ и его спецификации. Сумма документа рассчитывается автоматически.")
    @ApiResponse(responseCode = "201", description = "Документ создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "409", description = "Документ с таким номером уже существует")
    @PostMapping
    public ResponseEntity<Document> create(@Valid @RequestBody DocumentDto dto) {
        Document doc = documentService.createDocument(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }


    @Operation(summary = "Обновить документ")
    @ApiResponse(responseCode = "200", description = "Документ обновлён")
    @ApiResponse(responseCode = "404", description = "Документ не найден")
    @ApiResponse(responseCode = "409", description = "Номер документа уже занят")
    @PutMapping("/{id}")
    public ResponseEntity<Document> update(@PathVariable Long id, @Valid @RequestBody DocumentDto dto) {
        Document doc = documentService.updateDocument(id, dto);
        return ResponseEntity.ok(doc);
    }


    @Operation(summary = "Удалить документ", description = "Удаляет документ и все связанные спецификации.")
    @ApiResponse(responseCode = "204", description = "Документ удалён")
    @ApiResponse(responseCode = "404", description = "Документ не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    private DocumentResponseDto toResponseDto(Document doc) {
        var specDtos = doc.getSpecifications().stream()
                .map(spec -> new SpecificationResponseDto(
                        spec.getId(),
                        spec.getName(),
                        spec.getAmount()
                ))
                .toList();

        return new DocumentResponseDto(
                doc.getId(),
                doc.getNumber(),
                doc.getDate(),
                doc.getAmount(),
                doc.getNote(),
                specDtos
        );
    }


}

