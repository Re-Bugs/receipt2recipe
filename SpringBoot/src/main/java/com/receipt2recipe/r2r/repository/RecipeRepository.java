package com.receipt2recipe.r2r.repository;

import com.receipt2recipe.r2r.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, PagingAndSortingRepository<Recipe, Long> {
    List<Recipe> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r.id FROM Recipe r")
    List<Long> findAllRecipeIds();

    @Query("SELECT r FROM Recipe r " +
            "JOIN RecipeIgdt ri ON r.id = ri.recipe.id " +
            "WHERE ri.ingredient.id IN :ingredientIds " +
            "AND EXISTS (SELECT 1 FROM RecipeIgdt rii WHERE rii.recipe.id = r.id AND rii.id = " +
            "(SELECT MIN(riii.id) FROM RecipeIgdt riii WHERE riii.recipe.id = r.id) AND rii.ingredient.id IN :ingredientIds) " +
            "GROUP BY r.id " +
            "ORDER BY COUNT(ri.ingredient.id) DESC")
    List<Recipe> findTopRecipesByIngredientIds(@Param("ingredientIds") List<Long> ingredientIds, Pageable pageable);
}
