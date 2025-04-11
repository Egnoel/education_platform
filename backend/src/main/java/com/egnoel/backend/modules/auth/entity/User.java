package com.egnoel.backend.modules.auth.entity;

import com.egnoel.backend.modules.institution.entity.Institution;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(updatable = false)
    private final LocalDateTime create_time = LocalDateTime.now();
}
