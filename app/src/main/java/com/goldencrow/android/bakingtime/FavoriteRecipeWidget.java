package com.goldencrow.android.bakingtime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.goldencrow.android.bakingtime.adapters.RecipeAdapter;

/**
 * Implementation of App Widget functionality.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class FavoriteRecipeWidget extends AppWidgetProvider {

    public static final String WIDGET_FAV_KEY ="widget_favorite_recipe_key";
    public static final String WIDGET_RECIPE_NAME_KEY ="widget_favorite_recipe_name_key";
    public static final String WIDGET_INGREDIENTS_KEY ="widget_favorite_recipe_ingredients_key";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipeName, String ingredients) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_recipe_widget);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        String recipeDefaultName = "No Favorite Recipe";
        if (recipeName == null && ingredients == null) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);

            recipeName = sharedPreferences.getString(
                    RecipeAdapter.BOOKMARK_TITLE_KEY, recipeDefaultName);
            ingredients = sharedPreferences.getString(
                    RecipeAdapter.BOOKMARK_INGREDIENTS_KEY, "");
        }

        views.setTextViewText(R.id.widget_recipe_tv, recipeName);
        views.setTextViewText(R.id.widget_ingredients_tv, ingredients);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(WIDGET_FAV_KEY)) {
            Bundle bundle = intent.getBundleExtra(WIDGET_FAV_KEY);
            String recipeName = bundle.getString(WIDGET_RECIPE_NAME_KEY);
            String ingredients = bundle.getString(WIDGET_INGREDIENTS_KEY);

            AppWidgetManager man = AppWidgetManager.getInstance(context);
            int[] ids = man.getAppWidgetIds(
                    new ComponentName(context, FavoriteRecipeWidget.class));
            for (int appWidgetId : ids) {
                updateAppWidget(context,
                        AppWidgetManager.getInstance(context),
                        appWidgetId, recipeName, ingredients);
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null, null);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

