package com.receipt2recipe.r2r.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HeartDTO {
    private Long rcpId;
    private String rcpName;
    private String rcpCookingTime;
    private String rcpDifficulty;
    private String rcpImageUrl;
    private String rcpQuantity;
}
