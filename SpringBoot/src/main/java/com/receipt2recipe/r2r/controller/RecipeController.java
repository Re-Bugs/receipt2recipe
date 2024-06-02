package com.receipt2recipe.r2r.controller;

import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.domain.Recipe;
import com.receipt2recipe.r2r.domain.Review;
import com.receipt2recipe.r2r.dto.RecipeDTO;
import com.receipt2recipe.r2r.service.HeartService;
import com.receipt2recipe.r2r.service.RecipeService;
import com.receipt2recipe.r2r.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    private final HeartService heartService;
    private final ReviewService reviewService;

    @PostMapping("/like_recipe")
    public String likeRecipe(@RequestParam Long recipeId, HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        } else {
            model.addAttribute("userEmail", member.getUserEmail());
            model.addAttribute("userName", member.getUserName());
        }

        Recipe recipe = recipeService.getRecipeById(recipeId);
        model.addAttribute("recipe", recipe);

        List<String> fridgeIngredients = heartService.getFridgeIngredientsByUserEmail(member.getUserEmail());
        model.addAttribute("fridgeIngredients", fridgeIngredients);

        try {
            heartService.addFavoriteRecipe(member.getUserEmail(), recipeId);
            model.addAttribute("message", "찜 목록에 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "recipe/detail";
    }

    @PostMapping("/remove_favorite")
    public String removeFavorite(@RequestParam Long recipeId, HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        try {
            heartService.removeFavoriteRecipe(member.getUserEmail(), recipeId);
            model.addAttribute("message", "찜 목록에서 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my_fridge";
    }

    @GetMapping("/recipe/detail/{id}")
    public String getRecipeDetail(@PathVariable Long id, HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member != null) {
            model.addAttribute("userEmail", member.getUserEmail());
            model.addAttribute("userName", member.getUserName());
        }

        Recipe recipe = recipeService.getRecipeById(id);
        model.addAttribute("recipe", recipe);

        List<String> fridgeIngredients = heartService.getFridgeIngredientsByUserEmail(member.getUserEmail());
        model.addAttribute("fridgeIngredients", fridgeIngredients);

        List<Review> reviews = reviewService.getReviewsByRecipeId(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        reviews.forEach(review -> review.setFormattedModifiedDate(review.getModifiedDate().format(formatter)));

        model.addAttribute("reviews", reviews);

        return "recipe/detail";
    }

    @GetMapping("/recipe/search")
    public String searchRecipes(@RequestParam("q") String query, Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        List<RecipeDTO> recipes = recipeService.searchRecipesByName(query);
        model.addAttribute("recipes", recipes);
        model.addAttribute("userEmail", member.getUserEmail());
        model.addAttribute("userName", member.getUserName());
        return "recipe/search_results";
    }

    @PostMapping("/recipe/{id}/review")
    public String addReview(@PathVariable Long id, @RequestParam int rating, @RequestParam String comment, HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        Optional<Review> existingReview = reviewService.getReviewByUserEmailAndRecipeId(member.getUserEmail(), id);
        if (existingReview.isPresent()) {
            model.addAttribute("errorMessage", "이미 이 레시피에 리뷰를 남기셨습니다.");
            return "redirect:/recipe/detail/" + id;
        }

        Recipe recipe = recipeService.getRecipeById(id);
        Review review = new Review(member.getUserEmail(), recipe, rating, comment);
        reviewService.saveReview(review);

        return "redirect:/recipe/detail/" + id;
    }

}
