package com.receipt2recipe.r2r.service;

import com.receipt2recipe.r2r.domain.Fridge;
import com.receipt2recipe.r2r.domain.Ingredient;
import com.receipt2recipe.r2r.domain.Recipe;
import com.receipt2recipe.r2r.domain.RefAndIgdt;
import com.receipt2recipe.r2r.dto.RefAndIgdtDTO;
import com.receipt2recipe.r2r.exception.UniqueConstraintViolationException;
import com.receipt2recipe.r2r.repository.FridgeRepository;
import com.receipt2recipe.r2r.repository.IngredientRepository;
import com.receipt2recipe.r2r.repository.RecipeRepository;
import com.receipt2recipe.r2r.repository.RefAndIgdtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final IngredientRepository ingredientRepository;
    private final RefAndIgdtRepository refAndIgdtRepository;
    private final FridgeRepository fridgeRepository;
    private final RecipeRepository recipeRepository;

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public List<RefAndIgdt> getMyIngredients(Long fridgeId) {
        return refAndIgdtRepository.findByFridgeRfId(fridgeId);
    }

    public void addIngredientToFridge(Long fridgeId, Long ingredientId, LocalDate purchaseDate) {
        Fridge fridge = fridgeRepository.findById(fridgeId).orElseThrow(() -> new IllegalArgumentException("Invalid fridge ID"));

        // 중복 재료 체크
        boolean ingredientExists = refAndIgdtRepository.findByFridgeRfId(fridgeId).stream()
                .anyMatch(refAndIgdt -> refAndIgdt.getIngredient().getIgdtId().equals(ingredientId));
        if (ingredientExists) {
            throw new IllegalArgumentException("냉장고에 이미 있는 재료는 추가할 수 없습니다.");
        }

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 재료를 찾을 수 없습니다."));

        RefAndIgdt refAndIgdt = new RefAndIgdt();
        refAndIgdt.setFridge(fridge);
        refAndIgdt.setIngredient(ingredient);
        refAndIgdt.setPurchaseDate(java.sql.Date.valueOf(purchaseDate));

        refAndIgdtRepository.save(refAndIgdt);
    }

    public void deleteIngredientFromFridge(Long fridgeId, Long ingredientId) {
        RefAndIgdt refAndIgdt = refAndIgdtRepository.findByFridgeRfIdAndIngredientIgdtId(fridgeId, ingredientId)
                .orElseThrow(() -> new NoSuchElementException("재료를 찾을 수 없습니다."));
        refAndIgdtRepository.delete(refAndIgdt);
    }


    public List<Recipe> recommendTopRecipesByFridge(Long fridgeId) {
        List<Long> ingredientIds = refAndIgdtRepository.findByFridgeRfId(fridgeId)
                .stream()
                .map(refAndIgdt -> refAndIgdt.getIngredient().getIgdtId())
                .collect(Collectors.toList());

        return recipeRepository.findTopRecipesByIngredientIds(ingredientIds, PageRequest.of(0, 10));
    }

    public List<RefAndIgdtDTO> getAPIMyIngredients(Long fridgeId) {
        List<RefAndIgdt> refAndIgdts = refAndIgdtRepository.findByFridgeRfId(fridgeId);
        return refAndIgdts.stream()
                .map(refAndIgdt -> new RefAndIgdtDTO(
                        refAndIgdt.getIngredient().getIgdtId(),
                        refAndIgdt.getIngredient().getIgdtName(),
                        refAndIgdt.getPurchaseDate()))
                .collect(Collectors.toList());
    }

}
