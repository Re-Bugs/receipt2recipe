package com.receipt2recipe.r2r.api;

import com.receipt2recipe.r2r.domain.*;
import com.receipt2recipe.r2r.dto.*;
import com.receipt2recipe.r2r.exception.UniqueConstraintViolationException;
import com.receipt2recipe.r2r.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiMainController {
    private final RecipeService recipeService;
    private final FridgeService fridgeService;
    private final HeartService heartService;
    private final IngredientService ingredientService;
    private final ReviewService reviewService;

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

    @GetMapping("/search_ingredient")
    public List<Ingredient> searchIngredient(@RequestParam("q") String query) {
        return ingredientService.searchIngredientsByName(query);
    }

    @GetMapping("/search_recipe")
    public List<RecipeDTO> searchRecipes(@RequestParam("q") String query, HttpSession session) {
        return recipeService.searchRecipesByName(query);
    }

    @PostMapping("/add_ingredient")
    public ResponseEntity<Map<String, String>> addIngredient(@RequestParam("q") Long ingredientId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");

        Long fridgeId = member.getFridge().getRfId();
        Map<String, String> response = new HashMap<>();
        try {
            fridgeService.addIngredientToFridge(fridgeId, ingredientId, LocalDate.now());
            response.put("message", "Success");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            log.error("{}", e.getMessage());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            log.error("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/delete_ingredients")
    public ResponseEntity<Map<String, String>> deleteIngredients(@RequestParam("q") List<Long> ingredientIds, HttpSession session) {
        Member member = (Member) session.getAttribute("user");

        if (member == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long fridgeId = member.getFridge().getRfId();
        Map<String, String> response = new HashMap<>();
        try {
            for (Long ingredientId : ingredientIds) {
                fridgeService.deleteIngredientFromFridge(fridgeId, ingredientId);
            }
            response.put("message", "Success");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NoSuchElementException e) {
            log.error("{}", e.getMessage());
            response.put("message", "One or more ingredients not found");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("message", "Fail");
            log.error("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PostMapping("/add_heart")
    public ResponseEntity<Map<String, String>> addHeart(@RequestParam("q") Long recipeId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");

        Map<String, String> response = new HashMap<>();
        try {
            heartService.addFavoriteRecipe(member.getUserEmail(), recipeId);
            response.put("message", "Success");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            log.error("{}", e.getMessage());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("message", "Fail");
            log.error("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/delete_heart")
    public ResponseEntity<Map<String, String>> deleteHeart(@RequestParam("q") Long recipeId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");

        Map<String, String> response = new HashMap<>();
        try {
            heartService.removeFavoriteRecipe(member.getUserEmail(), recipeId);
            response.put("message", "Success");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            log.error("{}", e.getMessage());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "Fail");
            log.error("{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/add_review")
    public ResponseEntity<Map<String, String>> addReview(@RequestBody ReviewDTO reviewDTO, @RequestParam("q") Long rcpId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Map<String, String> response = new HashMap<>();

        try {
            recipeService.addReview(reviewDTO, member.getUserEmail(), rcpId);
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } catch (UniqueConstraintViolationException e) {
            log.error(e.getMessage());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            response.put("message", "Failed to add review");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



}
