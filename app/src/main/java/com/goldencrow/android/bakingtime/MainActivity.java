package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.goldencrow.android.bakingtime.adapters.RecipeAdapter;
import com.goldencrow.android.bakingtime.endpoints.RecipeEndpointInterface;
import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The activity which displays all the recipes available.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity
                        implements RecipeAdapter.RecipeOnClickListener {

    /**
     * The Debug-Tag used for logging errors, warning, .... in this class.
     */
    private final String TAG = this.getClass().getSimpleName();

    /**
     * The Loading Indicator (Donut) which shows that the data is still loading.
     */
    private ImageView mLoadingIndicatorIv;

    /**
     * The Adapter for the RecyclerView which handles all the data as well as the action for
     * each single element/card in it.
     */
    private RecipeAdapter mAdapter;

    /**
     * The Grid in which the recipe-cards are located. The column-count will change depending on
     * the rotation of the device and if it is a tablet or phone.
     */
    private GridLayoutManager layoutManager;

    /**
     * Sets up all variables and retrieves the recipes from the web to display them.
     *
     * @param savedInstanceState    the saved state before the device rotation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the GUI-Elements.
        RecyclerView recipeListRv = findViewById(R.id.recipe_list_rv);
        mLoadingIndicatorIv = findViewById(R.id.loading_indicator_iv);

        // Make the donut symbol rotate like a loading indicator.
        addSpinningEffectToLoader();

        // Setting up the RecyclerListView.
        int gridLayoutDefaultSpanCount = 1;
        layoutManager = new GridLayoutManager(this, gridLayoutDefaultSpanCount);

        // Change the column count of the grid depending on the rotation of the device and if
        //   it is a phone or tablet.
        changeLayoutSpanCountFitForDevice();

        // Set up the RecyclerView for usage.
        mAdapter = new RecipeAdapter(this, this);

        recipeListRv.setLayoutManager(layoutManager);
        recipeListRv.setHasFixedSize(true);
        recipeListRv.setAdapter(mAdapter);

        // Getting all the data from the network and set it to the adapter.
        RecipeEndpointInterface recipeEndpoint =
                NetworkUtil.getClient().create(RecipeEndpointInterface.class);

        final Call<Recipe[]> recipeCall = recipeEndpoint.doGetRecipes();
        recipeCall.enqueue(new Callback<Recipe[]>() {
            @Override
            public void onResponse(@NonNull Call<Recipe[]> call, @NonNull Response<Recipe[]> response) {
                // Remove the loading indicator.
                removeLoadingIndicator();
                // Get the recipes.
                Recipe[] recipes = response.body();
                // Set the recipes to the adapter.
                mAdapter.setRecipes(recipes);
            }

            @Override
            public void onFailure(@NonNull Call<Recipe[]> call, @NonNull Throwable t) {
                // If it failed, cancel the call and log the message.
                call.cancel();
                Log.w(TAG, t.getLocalizedMessage());
            }
        });
    }

    /**
     * On device rotation, change the column count of the grid.
     *
     * @param savedInstanceState    the saved variables and such.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        changeLayoutSpanCountFitForDevice();
    }

    /**
     * Changes the column count of the grid depending on the device and rotation.
     * Phone: 1 col (Land 2 col)
     * Tablet: 2 col (Land 3 col)
     */
    private void changeLayoutSpanCountFitForDevice() {
        int smallSpanCount = 1;
        int mediumSpanCount = 2;
        int largeSpanCount = 3;

        int orientation = getResources().getConfiguration().orientation;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager.setSpanCount(mediumSpanCount);
            } else {
                layoutManager.setSpanCount(largeSpanCount);
            }
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager.setSpanCount(smallSpanCount);
            } else {
                layoutManager.setSpanCount(mediumSpanCount);
            }
        }
    }

    /**
     * Adds a rotating animation on the Loading Indicator (Donut), so that the user sees
     * visual feedback that the app is still running and not frozen.
     */
    private void addSpinningEffectToLoader() {
        // A rotation takes 180 degrees.
        RotateAnimation rotate = new RotateAnimation(
                0, 180,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // and takes one second.
        rotate.setDuration(1000);
        // and it does it for infinity.
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        mLoadingIndicatorIv.startAnimation(rotate);
    }

    /**
     * Removes the Loading Indicator and its animation.
     * It is called when the data for the recipes is finished loading.
     */
    private void removeLoadingIndicator() {
        mLoadingIndicatorIv.clearAnimation();
        mLoadingIndicatorIv.setVisibility(View.INVISIBLE);
    }

    /**
     * If a click on a recipe happened, then open the detail activity.
     *
     * @param recipe    contains all the information of the recipe which was clicked upon.
     */
    @Override
    public void OnClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.RECIPE_KEY, recipe);
        startActivity(intent);
    }
}
