package com.receipt2recipe.r2r.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainRecipe {
    private int id;
    private String name;
    private String imageUrl;
    private String link;

    public MainRecipe(int id, String name, String imageUrl, String link) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.link = link;
    }
}
