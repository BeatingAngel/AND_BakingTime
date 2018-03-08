package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.entities.Step;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeMasterListFragment.OnStepClickListener {

    public static final String RECIPE_KEY = "RECIPE_KEY";
    public static final String RECIPE_STEPS_KEY = "STEP_KEY";
    public static final String RECIPE_STEP_POS_KEY = "STEP_POS_KEY";
    public static final String RECIPE_INGREDIENTS_KEY = "INGREDIENTS_KEY";

    RecipeMasterListFragment mRecipeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Recipe recipe;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RECIPE_KEY)) {
            recipe = intent.getParcelableExtra(RECIPE_KEY);

            mRecipeFragment = new RecipeMasterListFragment();
            mRecipeFragment.setRecipe(recipe);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.master_list_container, mRecipeFragment)
                    .commit();
        }
    }

    @Override
    public void onStepClick(int position, Step[] steps) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLandscape =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (isTablet && isLandscape && position != 0) {
            RecipeStepFragment stepFragment = new RecipeStepFragment();
            stepFragment.setData(steps, position - 1);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.master_detail_container, stepFragment)
                    .commit();

        } else if (position != 0) {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(RECIPE_STEP_POS_KEY, position - 1);

            ArrayList<Step> stepList = new ArrayList<>();
            stepList.addAll(Arrays.asList(steps));
            intent.putParcelableArrayListExtra(RECIPE_STEPS_KEY, stepList);

            startActivity(intent);
        }
    }
}
