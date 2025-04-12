package com.egnoel.backend.modules.classe.entity;

import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import com.egnoel.backend.modules.auth.entity.Student;
import com.egnoel.backend.modules.auth.entity.Teacher;
import com.egnoel.backend.modules.subject.entity.Subject;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "classes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "academic_year_id", "subject_id", "teacher_id"})
)
@Data
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 100, message = "O nome da turma deve ter até 100 caracteres")
    private String name;

    @Column(updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name = "classe_students",
            joinColumns = @JoinColumn(name = "classe_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students = new ArrayList<>();
}