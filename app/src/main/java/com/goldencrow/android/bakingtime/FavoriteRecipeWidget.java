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

    /**
     * This key is used to store a recipe in a Bundle. This bundle will be sent here if the
     * favorite recipe changed.
     * <p>
     * The data set into the bundle in the EntityUtil#setFavoriteRecipeToWidget method.
     * Those values will be extracted here in the #onReceive method.
     *
     * @see #onReceive(Context, Intent)
     * @see com.goldencrow.android.bakingtime.utils.EntityUtil#setFavoriteRecipeToWidget(Context, String, String)
     */
    public static final String WIDGET_FAV_KEY ="widget_favorite_recipe_key";

    /**
     * This key is the access to the name of the favorite recipe that will be shown in the widget.
     *
     * @see #onReceive(Context, Intent)
     * @see com.goldencrow.android.bakingtime.utils.EntityUtil#setFavoriteRecipeToWidget(Context, String, String)
     */
    public static final String WIDGET_RECIPE_NAME_KEY ="widget_favorite_recipe_name_key";

    /**
     * This key is the access to the list of ingredients of the favorite recipe that will be shown
     * in the widget.
     *
     * @see #onReceive(Context, Intent)
     * @see com.goldencrow.android.bakingtime.utils.EntityUtil#setFavoriteRecipeToWidget(Context, String, String)
     */
    public static final String WIDGET_INGREDIENTS_KEY ="widget_favorite_recipe_ingredients_key";

    /**
     * It checks if a favorite recipe exists, and if there is one, then it's data will be displayed
     * in this specific widget from the appWidgetId.
     * <p>
     * Additionally, a click on the widget opens the app and lands on the MainActivity.
     *
     * @param context           the context from the caller activity.
     * @param appWidgetManager  the AppWidgetManager from the context.
     * @param appWidgetId       the ID from a specific widget on the home menu
     *                          which will get updated.
     * @param recipeName        the name of the favorite recipe that will be displayed.
     * @param ingredients       the list of ingredients from the favorite recipe which will
     *                          be displayed alongside its name.
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipeName, String ingredients) {
        // Construct the RemoteViews object.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_recipe_widget);

        // Create the PendingIntent to open the app (start -> MainActivity).
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        // If this method wasn't called because a new recipe was set, then it was called because
        //   the timer runs out or a new widget was created.
        // If this is the case, then the favorite recipe information will be set to the widget
        //   from the stored data in the SharedPreferences.
        String recipeDefaultName = "No Favorite Recipe";
        if (recipeName == null && ingredients == null) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);

            recipeName = sharedPreferences.getString(
                    RecipeAdapter.BOOKMARK_TITLE_KEY, recipeDefaultName);
            ingredients = sharedPreferences.getString(
                    RecipeAdapter.BOOKMARK_INGREDIENTS_KEY, "");
        }

        // Set all data into the appropriate views.
        views.setTextViewText(R.id.widget_recipe_tv, recipeName);
        views.setTextViewText(R.id.widget_ingredients_tv, ingredients);

        // Instruct the widget manager to update the widget.
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * If the favorite recipe changed, then this method is triggered. It changes the
     * favorite recipe in the SharedPreferences for later usage and also updates all the
     * widgets.
     *
     * @param context   the context of the activity which called this method.
     * @param intent    the Intent that started this onReceive method.
     *                  It contains (probably) the data of a new favorite recipe.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // check if this method was launched to change the current favorite recipe.
        if (intent.hasExtra(WIDGET_FAV_KEY)) {
            // Extract all the information from the Bundle/Intent.
            Bundle bundle = intent.getBundleExtra(WIDGET_FAV_KEY);
            String recipeName = bundle.getString(WIDGET_RECIPE_NAME_KEY);
            String ingredients = bundle.getString(WIDGET_INGREDIENTS_KEY);

            // Get all ID's of the currently existing widgets on the Home Menu.
            AppWidgetManager man = AppWidgetManager.getInstance(context);
            int[] ids = man.getAppWidgetIds(
                    new ComponentName(context, FavoriteRecipeWidget.class));

            // Update all widgets to the new favorite recipe.
            for (int appWidgetId : ids) {
                updateAppWidget(context,
                        AppWidgetManager.getInstance(context),
                        appWidgetId, recipeName, ingredients);
            }
        } else {
            // ...otherwise, do what you want.
            super.onReceive(context, intent);
        }
    }

    /**
     * If the timer runs out, then all the widgets will be updated.
     *
     * @param context           the context from the caller activity.
     * @param appWidgetManager  the AppWidgetManager from the context.
     * @param appWidgetIds      the ids of all existing widgets on the Home Menu.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null, null);
        }
    }

    /**
     * Not implemented.
     *
     * @param context   the context from the caller activity.
     */
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    /**
     * Not implemented.
     *
     * @param context   the context from the caller activity.
     */
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

