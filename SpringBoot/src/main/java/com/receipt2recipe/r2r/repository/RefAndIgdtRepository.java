package com.receipt2recipe.r2r.repository;

import com.receipt2recipe.r2r.domain.RefAndIgdt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefAndIgdtRepository extends JpaRepository<RefAndIgdt, Long> {
    List<RefAndIgdt> findByFridgeRfId(Long fridgeId);

    // 특정 fridgeId와 ingredientId를 기반으로 재료 찾기
    Optional<RefAndIgdt> findByFridgeRfIdAndIngredientIgdtId(Long fridgeId, Long ingredientId);
}
