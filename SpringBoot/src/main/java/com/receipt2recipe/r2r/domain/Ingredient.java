package com.receipt2recipe.r2r.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "igdt_id")
    private Long igdtId;

    @Column(name = "igdt_name", unique = true, nullable = false)
    private String igdtName;
}
