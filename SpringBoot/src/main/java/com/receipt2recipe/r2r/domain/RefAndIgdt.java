package com.receipt2recipe.r2r.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "ref_and_igdt")
public class RefAndIgdt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ref_igdt_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rf_id")
    @JsonBackReference
    private Fridge fridge;

    @ManyToOne
    @JoinColumn(name = "igdt_id")
    private Ingredient ingredient;

    @Column(name = "purchase_date", nullable = false, updatable = false)
    private Date purchaseDate;

    @PrePersist
    protected void onCreate() {
        this.purchaseDate = new Date();
    }
}
