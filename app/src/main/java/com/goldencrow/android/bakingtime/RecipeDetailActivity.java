package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.goldencrow.android.bakingtime.entities.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String RECIPE_KEY = "RECIPE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Recipe recipe;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RECIPE_KEY)) {
            recipe = intent.getParcelableExtra(RECIPE_KEY);

            RecipeMasterListFragment recipeFragment = new RecipeMasterListFragment();
            recipeFragment.setRecipe(recipe);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.master_list_container, recipeFragment)
                    .commit();
        }
    }
}
