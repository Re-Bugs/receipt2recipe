package com.receipt2recipe.r2r.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RecipeDetailDTO {
    private String rcpName;
    private String rcpCookingTime;
    private String rcpDifficulty;
    private String rcpImageUrl;
    private String rcpQuantities;
    private List<RecipeStepDTO> rcpSteps;
    private List<RecipeIgdtDTO> rcpIgdt;
    private List<ReviewDTO> rcpReview;
}