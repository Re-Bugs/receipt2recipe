package com.receipt2recipe.r2r.repository;

import com.receipt2recipe.r2r.domain.RefAndIgdt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefAndIgdtRepository extends JpaRepository<RefAndIgdt, Long> {
    List<RefAndIgdt> findByFridgeRfId(Long fridgeId);
}