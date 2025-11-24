package ru.docapp.documentapp.services;

import ru.docapp.documentapp.dto.DocumentDto;
import ru.docapp.documentapp.dto.DuplicateLogEntry;
import ru.docapp.documentapp.exceptions.DuplicateDocumentNumberException;
import ru.docapp.documentapp.entities.Document;
import ru.docapp.documentapp.entities.Specification;
import ru.docapp.documentapp.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DuplicateLogService duplicateLogService;

    @Transactional
    public Document createDocument(DocumentDto dto) {
        if (documentRepository.findByNumber(dto.number()).isPresent()) {
            DuplicateLogEntry entry = new DuplicateLogEntry(
                    "DOCUMENT", dto.number(),
                    "Attempt to create document with duplicate number: " + dto.number()
            );
            duplicateLogService.logDuplicate(entry);
            throw new DuplicateDocumentNumberException("Document number '" + dto.number() + "' already exists");
        }

        Document doc = Document.builder()
                .number(dto.number())
                .date(dto.date())
                .note(dto.note())
                .build();

        // Сначала добавляем спецификации
        for (var specDto : dto.specifications()) {
            Specification spec = Specification.builder()
                    .name(specDto.name())
                    .amount(specDto.amount())
                    .document(doc)
                    .build();
            doc.addSpecification(spec);
        }

        // Пересчитываем ДО сохранения
        doc.recalculateAmount();

        // Теперь amount != null → можно сохранять
        return documentRepository.save(doc);
    }

    @Transactional
    public Document updateDocument(Long id, DocumentDto dto) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));

        // Проверка дубликата номера только если он изменился
        if (!doc.getNumber().equals(dto.number())) {
            if (documentRepository.findByNumber(dto.number()).isPresent()) {
                DuplicateLogEntry entry = new DuplicateLogEntry(
                        "DOCUMENT", dto.number(),
                        "Attempt to update document ID=" + id + " to duplicate number: " + dto.number()
                );
                duplicateLogService.logDuplicate(entry);
                throw new DuplicateDocumentNumberException("Document number '" + dto.number() + "' already exists");
            }
        }

        doc.setNumber(dto.number());
        doc.setDate(dto.date());
        doc.setNote(dto.note());

        // Очистка старых спецификаций и замена новыми
        doc.getSpecifications().clear();
        for (var specDto : dto.specifications()) {
            Specification spec = Specification.builder()
                    .id(specDto.id()) // null → создание, non-null → обновление (но проще пересоздать)
                    .name(specDto.name())
                    .amount(specDto.amount())
                    .document(doc)
                    .build();
            doc.addSpecification(spec);
        }

        doc.recalculateAmount();
        return documentRepository.save(doc);
    }

    @Transactional
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("Document not found: " + id);
        }
        documentRepository.deleteById(id);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentWithSpecifications(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    }
}