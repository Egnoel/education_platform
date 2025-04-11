package com.egnoel.backend.modules.material.repository;

import com.egnoel.backend.modules.material.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
}
