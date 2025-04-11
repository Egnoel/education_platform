package com.egnoel.backend.modules.auth.entity;


import com.egnoel.backend.modules.quiz.entity.Answer;
import com.egnoel.backend.modules.classe.entity.Classe;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
public class Student extends User{

    @Column(name = "student_number", unique = true)
    private String studentNumber;

    @ManyToMany
    @JoinTable(name = "student_classes",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "classe_id"))
    private List<Classe> classes;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Answer> answers;
}
