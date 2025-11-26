package ru.docapp.documentapp;

import ru.docapp.documentapp.dto.DocumentDto;
import ru.docapp.documentapp.dto.SpecificationDto;
import ru.docapp.documentapp.exceptions.DuplicateDocumentNumberException;
import ru.docapp.documentapp.entities.Document;
import ru.docapp.documentapp.repositories.DocumentRepository;
import ru.docapp.documentapp.services.DocumentService;
import ru.docapp.documentapp.services.DuplicateLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class  DocumentServiceTest{

@Autowired
private DocumentService documentService;

@Autowired
private DocumentRepository documentRepository;

@MockBean
private DuplicateLogService duplicateLogService;

@BeforeEach
void clean() {
    documentRepository.deleteAll();
}

@Test
void shouldCreateDocumentWithSpecifications() {
    var dto = new DocumentDto(
            null,
            "DOC-001",
            LocalDate.of(2025, 11, 25),
            null,
            "Test note",
            List.of(
                    new SpecificationDto(null, "Item 1", BigDecimal.valueOf(100.50)),
                    new SpecificationDto(null, "Item 2", BigDecimal.valueOf(200.00))
            )
    );

    Document saved = documentService.createDocument(dto);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getNumber()).isEqualTo("DOC-001");
    assertThat(saved.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(300.50));
    assertThat(saved.getSpecifications()).hasSize(2);
}

@Test
void shouldRejectDuplicateDocumentNumber() {
    var dto1 = new DocumentDto(null, "DOC-DUP", LocalDate.now(), null, "", List.of(new SpecificationDto(null, "X", BigDecimal.ONE)));
    documentService.createDocument(dto1);

    var dto2 = new DocumentDto(null, "DOC-DUP", LocalDate.now(), null, "", List.of(new SpecificationDto(null, "Y", BigDecimal.TEN)));

    assertThatThrownBy(() -> documentService.createDocument(dto2))
            .isInstanceOf(DuplicateDocumentNumberException.class)
            .hasMessageContaining("already exists");
}
}