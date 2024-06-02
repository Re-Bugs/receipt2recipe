package com.receipt2recipe.r2r.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "heart")
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fav_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "rcp_id")
    private Recipe recipe;
}
