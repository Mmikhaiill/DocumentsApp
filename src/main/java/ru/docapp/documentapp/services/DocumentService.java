package ru.docapp.documentapp.services;

import org.springframework.dao.DataIntegrityViolationException;
import ru.docapp.documentapp.dto.DocumentDto;
import ru.docapp.documentapp.dto.DuplicateLogEntry;
import ru.docapp.documentapp.dto.SpecificationDto;
import ru.docapp.documentapp.exceptions.DocumentNotFoundException;
import ru.docapp.documentapp.exceptions.DuplicateDocumentNumberException;
import ru.docapp.documentapp.entities.Document;
import ru.docapp.documentapp.entities.Specification;
import ru.docapp.documentapp.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DuplicateLogService duplicateLogService;

    @Transactional
    public Document createDocument(DocumentDto dto) {
        try {
            Document doc = mapFromDto(dto);
            doc.recalculateAmount();
            return documentRepository.save(doc);

        } catch (DataIntegrityViolationException ex) {
            duplicateLogService.logDuplicate(
                    new DuplicateLogEntry("DOCUMENT", dto.number(),
                            "Create failed - duplicate key")
            );
            throw new DuplicateDocumentNumberException(
                    "Document number already exists"
            );
        }
    }


    @Transactional
    public Document updateDocument(Long id, DocumentDto dto) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Not found: " + id));

        doc.setDate(dto.date());
        doc.setNote(dto.note());

        if (!doc.getNumber().equals(dto.number())) {
            doc.setNumber(dto.number());
        }

        syncSpecifications(doc, dto.specifications());

        doc.recalculateAmount();
        try {
            return documentRepository.save(doc);
        } catch (DataIntegrityViolationException e) {
            duplicateLogService.logDuplicate(
                    new DuplicateLogEntry("DOCUMENT", dto.number(),
                            "Update failed - duplicate key")
            );
            throw new DuplicateDocumentNumberException(
                    "Document number already exists"
            );
        }
    }

    @Transactional
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new DocumentNotFoundException("Document not found: " + id);
        }
        documentRepository.deleteById(id);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentWithSpecifications(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found: " + id));
    }

    private void syncSpecifications(Document doc, List<SpecificationDto> specs) {

        doc.getSpecifications()
                .removeIf(s -> specs.stream().noneMatch(dto -> dto.id() != null && dto.id().equals(s.getId())));

        for (var dto : specs) {
            Specification existing = null;

            if (dto.id() != null) {
                existing = doc.getSpecifications().stream()
                        .filter(s -> s.getId().equals(dto.id()))
                        .findFirst().orElse(null);
            }

            if (existing == null) {
                doc.addSpecification(
                        Specification.builder()
                                .name(dto.name())
                                .amount(dto.amount())
                                .document(doc)
                                .build()
                );
            } else {
                existing.setName(dto.name());
                existing.setAmount(dto.amount());
            }
        }
    }

    private Document mapFromDto(DocumentDto dto) {
        Document doc = new Document();
        doc.setNumber(dto.number());
        doc.setDate(dto.date());
        doc.setNote(dto.note());

        if (dto.specifications() != null) {
            dto.specifications().forEach(specDto -> {
                Specification spec = new Specification();
                spec.setName(specDto.name());
                spec.setAmount(specDto.amount());
                spec.setDocument(doc);
                doc.getSpecifications().add(spec);
            });
        }

        doc.recalculateAmount();
        return doc;
    }

}