package com.egnoel.backend.modules.auth.entity;


import com.egnoel.backend.modules.classe.entity.Classe;
import com.egnoel.backend.modules.institution.entity.Institution;
import com.egnoel.backend.modules.subject.entity.Subject;
import com.egnoel.backend.modules.material.entity.Material;
import com.egnoel.backend.modules.quiz.entity.Quiz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "user_id")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Teacher extends User{

    @Column(name = "employee_number",nullable = true  ,unique = true)
    private String employeeNumber;

    @ManyToMany
    @JoinTable(name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private List<Subject> subjects;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Material> materials;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Classe> classes;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Quiz> quizzes;
}
