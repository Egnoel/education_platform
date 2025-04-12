package com.egnoel.backend.modules.assessment.entity;


import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.classe.entity.Classe;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessments")
@Data
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100, message = "O título deve ter até 100 caracteres")
    private String title;

    @Column(nullable = false)
    @Min(value = 0, message = "A nota não pode ser inferior a 0")
    @Max(value = 20, message = "A nota não pode ser superior a 20")
    private Double grade;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;
}
