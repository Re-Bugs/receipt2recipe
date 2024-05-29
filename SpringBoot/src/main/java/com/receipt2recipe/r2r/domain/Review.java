package com.receipt2recipe.r2r.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rv_id")
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "rcp_id", nullable = false)
    private Recipe recipe;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Transient
    private String formattedModifiedDate;

    public Review() {
    }

    public Review(String userEmail, Recipe recipe, int rating, String comment) {
        this.userEmail = userEmail;
        this.recipe = recipe;
        this.rating = rating;
        this.comment = comment;
        this.modifiedDate = LocalDateTime.now();
    }

    public void setFormattedModifiedDate(String formattedModifiedDate) {
        this.formattedModifiedDate = formattedModifiedDate;
    }
}

