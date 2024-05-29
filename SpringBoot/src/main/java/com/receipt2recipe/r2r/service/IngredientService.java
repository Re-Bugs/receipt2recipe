package com.receipt2recipe.r2r.service;

import com.receipt2recipe.r2r.domain.Ingredient;
import com.receipt2recipe.r2r.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> searchIngredientsByName(String name) {
        return ingredientRepository.findByIgdtNameContainingIgnoreCase(name);
    }

    public List<String> getAllIngredientNames() {
        return ingredientRepository.findAllIngredientNames();
    }

    public Ingredient findByName(String name) {
        return ingredientRepository.findByIgdtName(name).orElseThrow(() -> new IllegalArgumentException("재료를 찾을 수 없습니다: " + name));
    }

    public Ingredient findById(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new NoSuchElementException("Ingredient not found"));
    }

    public List<String> findMatchingIngredients(String text) {
        List<String> ingredientNames = getAllIngredientNames();
        List<String> matchedIngredients = new ArrayList<>();

        for (String ingredient : ingredientNames) {
            if (text.contains(ingredient)) {
                matchedIngredients.add(ingredient);
            }
        }
        return matchedIngredients;
    }
}
