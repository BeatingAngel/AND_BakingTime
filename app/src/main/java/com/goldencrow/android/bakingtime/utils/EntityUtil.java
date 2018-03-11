package com.goldencrow.android.bakingtime.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.goldencrow.android.bakingtime.FavoriteRecipeWidget;
import com.goldencrow.android.bakingtime.entities.Ingredient;
import com.goldencrow.android.bakingtime.entities.Recipe;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Philipp
 */

public class EntityUtil {

    public static final String PREF_KEY = "com.goldencrow.android.bakingtime";
    public static final String PREF_KEY_RECIPE = "com.goldencrow.android.bakingtime.recipe";
    public static final String PREF_KEY_INGREDIENTS = "com.goldencrow.android.bakingtime.ingredients";

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
            if (!ingredient.getMeasure().toUpperCase().equals("UNIT")) {
                builder.append(ingredient.getMeasure());
            }
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

    public static void setFavoriteRecipe(Context context, String recipeName, String ingredients) {
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        Bundle bundle = new Bundle();
        bundle.putString(FavoriteRecipeWidget.WIDGET_RECIPE_NAME_KEY, recipeName);
        bundle.putString(FavoriteRecipeWidget.WIDGET_INGREDIENTS_KEY, ingredients);

        updateIntent.putExtra(FavoriteRecipeWidget.WIDGET_FAV_KEY, bundle);
        context.sendBroadcast(updateIntent);
    }

}
