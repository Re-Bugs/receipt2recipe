package com.receipt2recipe.r2r.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "recipe_igdt")
public class RecipeIgdt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rcp_igdt_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rcp_id", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "igdt_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "rcp_igdt_amount", nullable = false)
    private String amount;
}
