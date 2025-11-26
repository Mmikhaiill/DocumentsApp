package ru.docapp.documentapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.docapp.documentapp.controllers.DocumentController;
import ru.docapp.documentapp.dto.DocumentDto;
import ru.docapp.documentapp.dto.SpecificationDto;
import ru.docapp.documentapp.entities.Document;
import ru.docapp.documentapp.entities.Specification;
import ru.docapp.documentapp.exceptions.DocumentNotFoundException;
import ru.docapp.documentapp.services.DocumentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentService documentService;

    private DocumentDto validDto;
    private Document document;

    @BeforeEach
    void setUp() {
        validDto = new DocumentDto(
                null,
                "DOC-001",
                LocalDate.of(2025, 11, 25),
                null,
                "Test",
                List.of(new SpecificationDto(null, "Item", BigDecimal.valueOf(100.50)))
        );

        document = Document.builder()
                .id(1L)
                .number("DOC-001")
                .date(LocalDate.of(2025, 11, 25))
                .amount(BigDecimal.valueOf(100.50))
                .note("Test")
                .specifications(List.of(
                        Specification.builder()
                                .id(1L)
                                .name("Item")
                                .amount(BigDecimal.valueOf(100.50))
                                .build()
                ))
                .build();
    }

    // ================= CREATE =================

    @Test
    void shouldCreateDocumentSuccessfully() throws Exception {
        when(documentService.createDocument(any())).thenReturn(document);

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("DOC-001"))
                .andExpect(jsonPath("$.amount").value(100.50));

        verify(documentService).createDocument(any(DocumentDto.class));
    }

    // ================= UPDATE =================

    @Test
    void shouldUpdateDocumentSuccessfully() throws Exception {
        when(documentService.updateDocument(eq(1L), any())).thenReturn(document);

        mockMvc.perform(put("/api/documents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("DOC-001"));

        verify(documentService).updateDocument(eq(1L), any(DocumentDto.class));
    }

    // ================= DELETE =================

    @Test
    void shouldDeleteDocument() throws Exception {
        doNothing().when(documentService).deleteDocument(eq(1L));

        mockMvc.perform(delete("/api/documents/1"))
                .andExpect(status().isNoContent());

        verify(documentService).deleteDocument(eq(1L));
    }

    @Test
    void shouldReturn404OnDeleteNonExistentDocument() throws Exception {
        doThrow(new DocumentNotFoundException("Document not found: 999"))
                .when(documentService).deleteDocument(eq(999L));

        mockMvc.perform(delete("/api/documents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Document not found: 999"));
    }

    // ================= READ =================

    @Test
    void shouldGetAllDocuments() throws Exception {
        when(documentService.getAllDocuments()).thenReturn(Arrays.asList(document));

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].number").value("DOC-001"));
    }

    @Test
    void shouldGetDocumentById() throws Exception {
        when(documentService.getDocumentWithSpecifications(eq(1L))).thenReturn(document);

        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("DOC-001"));
    }

    @Test
    void shouldReturn404WhenDocumentNotFound() throws Exception {
        when(documentService.getDocumentWithSpecifications(eq(999L)))
                .thenThrow(new DocumentNotFoundException("Document not found: 999"));

        mockMvc.perform(get("/api/documents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Document not found: 999"));
    }

    // ================= VALIDATION =================

    @Test
    void shouldReturn400OnInvalidDto() throws Exception {
        var invalidDto = new DocumentDto(null, "", null, null, "", List.of());

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());

        verify(documentService, never()).createDocument(any());
    }
}