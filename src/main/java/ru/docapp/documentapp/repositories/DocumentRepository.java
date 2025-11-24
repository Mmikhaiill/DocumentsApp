package ru.docapp.documentapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.docapp.documentapp.entities.Document;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByNumber(String number);

    @Modifying
    @Transactional
    @Query("UPDATE Document d SET d.amount = (SELECT COALESCE(SUM(s.amount), 0) FROM Specification s WHERE s.document = d) WHERE d.id = :id")
    void updateAmountById(Long id);
}