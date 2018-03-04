package com.goldencrow.android.bakingtime.utils;

import com.goldencrow.android.bakingtime.entities.Ingredient;

/**
 * Created by Philipp
 */

public class EntityUtil {

    public static String getAllIngredientsAsOneString(Ingredient[] ingredients) {
        StringBuilder builder = new StringBuilder();

        for (Ingredient ingredient : ingredients) {

            float quantity = ingredient.getQuantity();
            if (quantity%1 == 0) {
                builder.append(String.valueOf((int)quantity));
            } else {
                builder.append(String.valueOf(quantity));
            }

            builder.append(" ");
            builder.append(ingredient.getMeasure());
            builder.append(" ");
            builder.append(ingredient.getIngredient());

            if (ingredients[ingredients.length - 1] != ingredient) {
                builder.append(", ");
            } else {
                builder.append(" and ");
            }
        }

        return builder.toString();
    }

    public static String getAllIngredientsAsAnEnumerationString(Ingredient[] ingredients) {
        StringBuilder builder = new StringBuilder();

        for (Ingredient ingredient : ingredients) {

            builder.append("* ");

            float quantity = ingredient.getQuantity();
            if (quantity%1 == 0) {
                builder.append(String.valueOf((int)quantity));
            } else {
                builder.append(String.valueOf(quantity));
            }

            builder.append(" ");
            builder.append(ingredient.getMeasure());
            builder.append(" ");
            builder.append(ingredient.getIngredient());

            builder.append("\n");
        }

        return builder.toString();
    }

}
