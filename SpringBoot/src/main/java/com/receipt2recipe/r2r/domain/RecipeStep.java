package com.receipt2recipe.r2r.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rcp_and_seq")
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @Column(name = "step_number", nullable = false)
    private int stepNumber;

    @Column(name = "step_description", nullable = false)
    private String description;

    @Column(name = "step_url")
    private String stepUrl;

    @ManyToOne
    @JoinColumn(name = "rcp_id", nullable = false)
    @JsonBackReference
    private Recipe recipe;
}
