package com.receipt2recipe.r2r.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class RefAndIgdtDTO {
    private Long igdtId;
    private String name;
    private Date purchaseDate;
}
