package com.receipt2recipe.r2r.api;

import com.receipt2recipe.r2r.domain.*;
import com.receipt2recipe.r2r.dto.HeartDTO;
import com.receipt2recipe.r2r.dto.RecipeDTO;
import com.receipt2recipe.r2r.dto.RecipeDetailDTO;
import com.receipt2recipe.r2r.dto.RefAndIgdtDTO;
import com.receipt2recipe.r2r.service.FridgeService;
import com.receipt2recipe.r2r.service.HeartService;
import com.receipt2recipe.r2r.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiMainController {
    private final RecipeService recipeService;
    private final FridgeService fridgeService;
    private final HeartService heartService;

    @GetMapping("/all_rcplist")
    public List<RecipeDTO> getAllRecipes() {
        return recipeService.getAllRecipeDTOs();
    }

    @GetMapping("/5_rcplist")
    public List<RecipeDTO> get5Recipes() {
        return recipeService.getTop5RecipeDTOs();
    }

    @GetMapping("/my_igdts")
    public List<RefAndIgdtDTO> getMyIngredients(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Long fridgeId = member.getFridge().getRfId(); // Member와 연결된 Fridge의 ID를 가져옴
        return fridgeService.getAPIMyIngredients(fridgeId);
    }
    @GetMapping("/my_hearts")
    public List<HeartDTO> getMyHearts(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        return heartService.getHeartsByUserEmail(member.getUserEmail());
    }

    @GetMapping("/detail/{id}")
    public RecipeDetailDTO getRecipeDetail(@PathVariable Long id, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Long fridgeId = member.getFridge().getRfId();
        return recipeService.getRecipeDetail(id, fridgeId);
    }

    @GetMapping("/recommend_recipes")
    public List<RecipeDTO> RecipeRecommend(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Long fridgeId = member.getFridge().getRfId();
        List<Recipe> recommendedRecipes = fridgeService.recommendTopRecipesByFridge(fridgeId);
        return recommendedRecipes.stream()
                .map(recipe -> new RecipeDTO(
                        recipe.getId(),
                        recipe.getName(),
                        recipe.getCookingTime(),
                        recipe.getDifficulty(),
                        recipe.getImageUrl(),
                        recipe.getQuantities()))
                .collect(Collectors.toList());
    }

}
