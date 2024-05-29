package com.receipt2recipe.r2r.repository;

import com.receipt2recipe.r2r.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByIgdtNameContainingIgnoreCase(String igdtName);

    @Query("SELECT i.igdtName FROM Ingredient i")
    List<String> findAllIngredientNames();

    Optional<Ingredient> findByIgdtName(String igdtName);
}
