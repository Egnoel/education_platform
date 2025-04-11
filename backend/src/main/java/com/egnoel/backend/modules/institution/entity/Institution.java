package com.egnoel.backend.modules.institution.entity;


import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import com.egnoel.backend.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "institutions")
@Data
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    private String contact;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AcademicYear> academicYears;
}
