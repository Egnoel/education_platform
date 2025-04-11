package com.egnoel.backend.modules.material.entity;

import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "materials")
@Data
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String filePath;

    @Column(updatable = false)
    private LocalDateTime uploadDate= LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "classe_id")
    private Classe classe;
}
