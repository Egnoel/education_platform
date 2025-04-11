package com.egnoel.backend.modules.academicyear.entity;


import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.institution.entity.Institution;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "academic_year")
@Data
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Classe> classes;
}
