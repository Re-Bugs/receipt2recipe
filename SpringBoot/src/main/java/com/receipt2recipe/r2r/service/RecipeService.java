package com.receipt2recipe.r2r.service;

import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.domain.Recipe;
import com.receipt2recipe.r2r.domain.Review;
import com.receipt2recipe.r2r.dto.*;
import com.receipt2recipe.r2r.exception.UniqueConstraintViolationException;
import com.receipt2recipe.r2r.repository.MemberRepository;
import com.receipt2recipe.r2r.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final ReviewService reviewService;
    private final MemberRepository memberRepository;
    private final FridgeService fridgeService;

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
    }

    public List<RecipeDTO> getAllRecipeDTOs() {
        List<Long> recipeIds = recipeRepository.findAllRecipeIds(); // 모든 레시피 ID를 가져옴
        List<RecipeDTO> recipeDTOs = new ArrayList<>();

        for (Long recipeId : recipeIds) {
            Optional<Recipe> recipe = recipeRepository.findById(recipeId);
            recipe.ifPresent(r -> recipeDTOs.add(new RecipeDTO(
                    r.getId(),
                    r.getName(),
                    r.getCookingTime(),
                    r.getDifficulty(),
                    r.getImageUrl(),
                    r.getQuantities()
            )));
        }

        return recipeDTOs;
    }

    public List<RecipeDTO> getTop5RecipeDTOs() {
        List<Recipe> top5Recipes = recipeRepository.findAll(PageRequest.of(2, 5)).getContent();
        return top5Recipes.stream()
                .map(r -> new RecipeDTO(
                        r.getId(),
                        r.getName(),
                        r.getCookingTime(),
                        r.getDifficulty(),
                        r.getImageUrl(),
                        r.getQuantities()))
                .collect(Collectors.toList());
    }

    public RecipeDetailDTO getRecipeDetail(Long id, Long fridgeId) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id: " + id));

        Set<Long> fridgeIngredientIds = fridgeService.getMyIngredients(fridgeId).stream()
                .map(refAndIgdt -> refAndIgdt.getIngredient().getIgdtId())
                .collect(Collectors.toSet());

        List<RecipeStepDTO> steps = recipe.getSteps().stream()
                .map(step -> new RecipeStepDTO(step.getStepNumber(), step.getDescription(), step.getStepUrl()))
                .collect(Collectors.toList());

        List<RecipeIgdtDTO> ingredients = recipe.getRecipeIgdts().stream()
                .map(igdt -> new RecipeIgdtDTO(igdt.getIngredient().getIgdtName(), fridgeIngredientIds.contains(igdt.getIngredient().getIgdtId())))
                .collect(Collectors.toList());

        List<ReviewDTO> reviews = reviewService.getReviewsByRecipeId(id).stream()
                .map(review -> new ReviewDTO(
                        review.getUserEmail(),
                        review.getRating(),
                        review.getComment(),
                        review.getModifiedDate()))
                .collect(Collectors.toList());

        return new RecipeDetailDTO(
                recipe.getName(),
                recipe.getCookingTime(),
                recipe.getDifficulty(),
                recipe.getImageUrl(),
                recipe.getQuantities(),
                steps,
                ingredients,
                reviews
        );
    }

    public List<RecipeDTO> searchRecipesByName(String query) {
        return recipeRepository.findByNameContainingIgnoreCase(query).stream()
                .map(recipe -> new RecipeDTO(
                        recipe.getId(),
                        recipe.getName(),
                        recipe.getCookingTime(),
                        recipe.getDifficulty(),
                        recipe.getImageUrl(),
                        recipe.getQuantities()))
                .collect(Collectors.toList());
    }

    public void addReview(ReviewDTO reviewDTO, String userEmail, Long rcpId) {
        // Recipe와 Member 객체를 찾아서 설정
        Recipe recipe = getRecipeById(rcpId);
        Member member = memberRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));

        // 중복 리뷰 체크
        Optional<Review> existingReview = reviewService.getReviewByUserEmailAndRecipeId(userEmail, rcpId);
        if (existingReview.isPresent()) {
            throw new UniqueConstraintViolationException("already reviewed this recipe.");
        }

        // Review 객체 생성 및 설정
        Review review = new Review();
        review.setRecipe(recipe);
        review.setUserEmail(userEmail);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setModifiedDate(LocalDateTime.now());

        // Review 저장
        reviewService.saveReview(review);
    }
}
