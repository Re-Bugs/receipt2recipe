package com.receipt2recipe.r2r.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "fridge")
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rf_id")
    private Long rfId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email")
    @JsonBackReference
    private Member member;

    @OneToMany(mappedBy = "fridge")
    @JsonManagedReference
    private List<RefAndIgdt> ingredients;

    public Fridge() {
    }

    public Fridge(Member member) {
        this.member = member;
    }
}
