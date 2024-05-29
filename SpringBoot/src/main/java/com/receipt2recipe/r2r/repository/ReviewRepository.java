package com.receipt2recipe.r2r.repository;

import com.receipt2recipe.r2r.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRecipeId(Long recipeId);

    Optional<Review> findByUserEmailAndRecipeId(String userEmail, Long recipeId);
}
