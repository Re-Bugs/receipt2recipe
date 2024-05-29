package com.receipt2recipe.r2r.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rcp_id")
    private Long id;

    @Column(name = "rcp_name", nullable = false)
    private String name;

    @Column(name = "rcp_cooking_time")
    private String cookingTime;

    @Column(name = "rcp_difficulty")
    private String difficulty;

    @Column(name = "rcp_image_url")
    private String imageUrl;

    @Column(name = "rcp_quantities")
    private String quantities;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RecipeStep> steps;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RecipeIgdt> recipeIgdts;
}
