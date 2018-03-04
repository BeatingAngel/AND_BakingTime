package com.goldencrow.android.bakingtime.entities;

/**
 * Created by Philipp
 */

public class Recipe {

    private int id;
    private String name;
    private Ingredient[] ingredients;
    private Step[] steps;
    private int servings;
    private String image;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public Step[] getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }
}
