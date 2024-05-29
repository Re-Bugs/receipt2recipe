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
        try {
            Fridge fridge = fridgeRepository.findById(fridgeId).orElseThrow(() -> new IllegalArgumentException("Invalid fridge ID"));
            Ingredient ingredient = new Ingredient();
            ingredient.setIgdtId(ingredientId);

            RefAndIgdt refAndIgdt = new RefAndIgdt();
            refAndIgdt.setFridge(fridge);
            refAndIgdt.setIngredient(ingredient);
            refAndIgdt.setPurchaseDate(java.sql.Date.valueOf(purchaseDate));

            refAndIgdtRepository.save(refAndIgdt);
        } catch (DataIntegrityViolationException e){
            throw new UniqueConstraintViolationException("냉장고에 이미 있는 재료는 추가할 수 없습니다.");
        }
    }

    public void deleteIngredientFromFridge(Long refIgdtId) {
        refAndIgdtRepository.deleteById(refIgdtId);
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
                .map(refAndIgdt -> new RefAndIgdtDTO(refAndIgdt.getIngredient(), refAndIgdt.getPurchaseDate()))
                .collect(Collectors.toList());
    }
}
