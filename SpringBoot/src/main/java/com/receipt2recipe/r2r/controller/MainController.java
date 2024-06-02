package com.receipt2recipe.r2r.controller;

import com.receipt2recipe.r2r.domain.*;
import com.receipt2recipe.r2r.dto.RecipeDTO;
import com.receipt2recipe.r2r.dto.RefAndIgdtDTO;
import com.receipt2recipe.r2r.exception.UniqueConstraintViolationException;
import com.receipt2recipe.r2r.service.FridgeService;
import com.receipt2recipe.r2r.service.HeartService;
import com.receipt2recipe.r2r.service.IngredientService;
import com.receipt2recipe.r2r.service.RecipeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final FridgeService fridgeService;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final HeartService heartService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String showRecipes(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member != null) {
            model.addAttribute("userEmail", member.getUserEmail());
            model.addAttribute("userName", member.getUserName());
        }
        List<RecipeDTO> recipes = recipeService.getAllRecipeDTOs();

        model.addAttribute("recipes", recipes);
        return "main/main";
    }

    @GetMapping("/my_fridge")
    public String myFridge(HttpSession session, Model model, @RequestParam(value = "message", required = false) String message, @RequestParam(value = "errorMessage", required = false) String errorMessage) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        Long fridgeId = member.getFridge().getRfId();
        List<Ingredient> allIngredients = fridgeService.getAllIngredients();
        List<RefAndIgdt> myIngredients = fridgeService.getMyIngredients(fridgeId);
        List<Heart> favoriteRecipes = heartService.getUserFavorites(member.getUserEmail());

        model.addAttribute("userEmail", member.getUserEmail());
        model.addAttribute("userName", member.getUserName());
        model.addAttribute("allIngredients", allIngredients);
        model.addAttribute("myIngredients", myIngredients);
        model.addAttribute("favoriteRecipes", favoriteRecipes);

        if (message != null) {
            model.addAttribute("message", message);
        }

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "main/my_fridge";
    }

    @PostMapping("/add_ingredient")
    public String addIngredient(@RequestParam(value = "ingredientId", required = false) Long ingredientId, HttpSession session, Model model) throws UnsupportedEncodingException {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        if (ingredientId == null) {
            String errorMessage = "재료를 선택하지 않았습니다.";
            return "redirect:/my_fridge?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
        }

        Long fridgeId = member.getFridge().getRfId();
        try {
            fridgeService.addIngredientToFridge(fridgeId, ingredientId, LocalDate.now());
            String message = "재료가 성공적으로 추가되었습니다.";
            return "redirect:/my_fridge?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (IllegalArgumentException e) {
            String errorMessage = "냉장고에 이미 있는 재료는 추가할 수 없습니다.";
            log.error("{}", e.getMessage());
            return "redirect:/my_fridge?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("{}", e.getMessage());
            return "redirect:/my_fridge?errorMessage=오류가 발생했습니다.";
        }
    }

    @GetMapping("/search_ingredient")
    @ResponseBody
    public List<Ingredient> searchIngredient(@RequestParam("q") String query) {
        return ingredientService.searchIngredientsByName(query);
    }

    @PostMapping("/delete_ingredient")
    public String deleteIngredient(@RequestParam("ingredientId") Long ingredientId, HttpSession session) throws UnsupportedEncodingException {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        Long fridgeId = member.getFridge().getRfId();
        try {
            fridgeService.deleteIngredientFromFridge(fridgeId, ingredientId);
            String message = "재료가 성공적으로 삭제되었습니다.";
            return "redirect:/my_fridge?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (NoSuchElementException e) {
            String errorMessage = "해당 재료가 냉장고에 존재하지 않습니다.";
            log.error("{}", e.getMessage());
            return "redirect:/my_fridge?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
        }
    }

    @GetMapping("/recommend_recipes")
    public String recommendRecipes(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/sign_in";
        }

        Long fridgeId = member.getFridge().getRfId();
        List<Recipe> recommendedRecipes = fridgeService.recommendTopRecipesByFridge(fridgeId);

        model.addAttribute("recommendedRecipes", recommendedRecipes);
        model.addAttribute("userEmail", member.getUserEmail());
        model.addAttribute("userName", member.getUserName());
        return "receipt/recommend_recipes";
    }
}
