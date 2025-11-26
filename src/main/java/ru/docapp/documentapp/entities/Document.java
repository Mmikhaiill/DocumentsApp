package ru.docapp.documentapp.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document", indexes = @Index(columnList = "number", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    private String note;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @Builder.Default
    @JsonManagedReference
    private List<Specification> specifications = new ArrayList<>();

    public void recalculateAmount() {
        this.amount = specifications.stream()
                .map(Specification::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addSpecification(Specification spec) {
        spec.setDocument(this);
        this.specifications.add(spec);
    }

    public void removeSpecification(Specification spec) {
        spec.setDocument(null);
        this.specifications.remove(spec);
    }
}