package com.receipt2recipe.r2r.service;

import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.domain.Recipe;
import com.receipt2recipe.r2r.domain.Review;
import com.receipt2recipe.r2r.dto.ReviewDTO;
import com.receipt2recipe.r2r.repository.MemberRepository;
import com.receipt2recipe.r2r.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByRecipeId(Long recipeId) {
        return reviewRepository.findByRecipeId(recipeId);
    }

    public Optional<Review> getReviewByUserEmailAndRecipeId(String userEmail, Long recipeId) {
        return reviewRepository.findByUserEmailAndRecipeId(userEmail, recipeId);
    }

    public void saveReview(Review review) {
        reviewRepository.save(review);
    }
}
