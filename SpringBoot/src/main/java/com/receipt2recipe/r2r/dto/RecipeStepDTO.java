package com.receipt2recipe.r2r.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecipeStepDTO {
    private int stepNumber;
    private String description;
    private String stepUrl;
}
