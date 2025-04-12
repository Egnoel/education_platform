package com.egnoel.backend.modules.institution.entity;


import com.egnoel.backend.modules.academicyear.entity.AcademicYear;
import com.egnoel.backend.modules.auth.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(nullable = false, unique = true)
    @Size(max = 100, message = "O nome da instituição deve ter até 100 caracteres")
    private String name;

    @Size(max = 255, message = "A localização deve ter até 255 caracteres")
    private String location;

    @Size(max = 100, message = "O contacto deve ter até 100 caracteres")
    private String contact;

    @Column(updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AcademicYear> academicYears = new ArrayList<>();
}
