package com.egnoel.backend.modules.subject.entity;


import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.institution.entity.Institution;
import com.egnoel.backend.modules.material.entity.Material;
import com.egnoel.backend.modules.quiz.entity.Quiz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String code;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @OneToMany(mappedBy = "subject")
    private List<Classe> classes;

    @OneToMany(mappedBy = "subject")
    private List<Material> materials;

    @OneToMany(mappedBy = "subject")
    private List<Quiz> quizzes;
}
