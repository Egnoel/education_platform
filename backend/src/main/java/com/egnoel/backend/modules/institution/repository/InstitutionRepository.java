package com.egnoel.backend.modules.institution.repository;

import com.egnoel.backend.modules.institution.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    // Custom query methods can be defined here if needed
}
