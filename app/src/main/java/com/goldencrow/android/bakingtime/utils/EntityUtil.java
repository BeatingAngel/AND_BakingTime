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
 * Contains all methods which handle/alter majorly the entities.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class EntityUtil {

    /**
     * Builds a string which looks like an enumeration of all ingredients.
     *
     * @param ingredients   the array of ingredients.
     * @return              a single string containing a list of ingredients.
     */
    public static String getAllIngredientsAsAnEnumerationString(Ingredient[] ingredients) {
        StringBuilder builder = new StringBuilder();
        String enumerationSymbol = "* ";

        for (Ingredient ingredient : ingredients) {

            builder.append(enumerationSymbol);

            // natural numbers don't need a comma.
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

    /**
     * Sends a broadcast to the Widget so that all the widget's will get updated to display
     * the new favorite recipe.
     *
     * @param context       the context which will send the Broadcast.
     * @param recipeName    the name of the recipe.
     * @param ingredients   a String containing an enumeration of all ingredients.
     */
    public static void setFavoriteRecipeToWidget(Context context, String recipeName, String ingredients) {
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        Bundle bundle = new Bundle();
        bundle.putString(FavoriteRecipeWidget.WIDGET_RECIPE_NAME_KEY, recipeName);
        bundle.putString(FavoriteRecipeWidget.WIDGET_INGREDIENTS_KEY, ingredients);

        updateIntent.putExtra(FavoriteRecipeWidget.WIDGET_FAV_KEY, bundle);
        context.sendBroadcast(updateIntent);
    }

}
