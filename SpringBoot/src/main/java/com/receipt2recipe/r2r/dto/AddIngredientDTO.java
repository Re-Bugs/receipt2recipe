package com.receipt2recipe.r2r.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class AddIngredientDTO {
    private Long fridgeId;
    private Long ingredientId;
    private String quantity;
    private String purchaseDate;
    private String expirationDate;
}