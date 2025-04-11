package com.egnoel.backend.modules.classe.repository;

import com.egnoel.backend.modules.classe.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClasseRepository extends JpaRepository<Classe, Long> {
    Classe findByName(String name);
}
