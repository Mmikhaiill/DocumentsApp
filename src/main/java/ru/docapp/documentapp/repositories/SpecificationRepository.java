package ru.docapp.documentapp.repositories;


import ru.docapp.documentapp.entities.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecificationRepository extends JpaRepository<Specification, Long> {
}
