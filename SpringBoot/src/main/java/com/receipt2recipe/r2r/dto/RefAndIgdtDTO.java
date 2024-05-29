package com.receipt2recipe.r2r.dto;

import com.receipt2recipe.r2r.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
public class RefAndIgdtDTO {
    private Ingredient ingredient;
    private Date purchaseDate;
}
