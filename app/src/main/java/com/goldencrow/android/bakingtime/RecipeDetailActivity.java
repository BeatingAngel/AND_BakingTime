package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.entities.Step;
import com.goldencrow.android.bakingtime.utils.EntityUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeMasterListFragment.OnStepClickListener {

    public static final String RECIPE_KEY = "RECIPE_KEY";
    public static final String RECIPE_STEPS_KEY = "STEP_KEY";
    public static final String RECIPE_STEP_POS_KEY = "STEP_POS_KEY";

    RecipeMasterListFragment mMasterListFragment;

    Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RECIPE_KEY)) {
            mRecipe = intent.getParcelableExtra(RECIPE_KEY);

            setTitle(mRecipe.getName());

            mMasterListFragment = new RecipeMasterListFragment();
            mMasterListFragment.setRecipe(mRecipe);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.master_list_container, mMasterListFragment)
                    .commit();
        }
    }

    @Override
    public void onStepClick(int position, Step[] steps) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            Step[] mySteps = steps;
            int pos = position - 1;
            if (position == 0) {
                mySteps = new Step[] {new Step(
                        EntityUtil.getAllIngredientsAsAnEnumerationString(mRecipe.getIngredients())
                )};
                pos = position;
            }

            RecipeMasterDetailFragment masterDetailFragment = new RecipeMasterDetailFragment();
            masterDetailFragment.setData(mySteps, pos);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.master_detail_container, masterDetailFragment)
                    .commit();
        } else {
            ArrayList<Step> stepList = new ArrayList<>();
            int pos = position - 1;
            if (position == 0) {
                stepList.add(new Step(
                        EntityUtil.getAllIngredientsAsAnEnumerationString(mRecipe.getIngredients())
                ));
                pos = position;
            } else {
                stepList.addAll(Arrays.asList(steps));
            }

            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(RECIPE_STEP_POS_KEY, pos);
            intent.putParcelableArrayListExtra(RECIPE_STEPS_KEY, stepList);

            startActivity(intent);
        }
    }
}
