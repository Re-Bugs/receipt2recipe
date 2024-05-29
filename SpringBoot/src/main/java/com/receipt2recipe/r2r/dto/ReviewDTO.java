package com.receipt2recipe.r2r.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReviewDTO {
    private String userEmail;
    private int rating;
    private String comment;
    private LocalDateTime modifiedDate;
}