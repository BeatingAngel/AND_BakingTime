package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.entities.Step;
import com.goldencrow.android.bakingtime.utils.EntityUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static com.goldencrow.android.bakingtime.RecipeStepActivity.RECIPE_STEPS_KEY;
import static com.goldencrow.android.bakingtime.RecipeStepActivity.RECIPE_STEP_POS_KEY;

/**
 * Activity which displays the list of steps for the phone and the master-detail view for the tablet.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeMasterListFragment.OnStepClickListener {

    /**
     * This key is used to store a parcelable recipe in an intent and send it to this activity.
     *
     * @see MainActivity#OnClick(Recipe)
     */
    public static final String RECIPE_KEY = "RECIPE_KEY";

    /**
     * The recipe which was clicked upon.
     */
    private Recipe mRecipe;

    /**
     * The fragment which displays the list of steps for the recipe.
     */
    RecipeMasterListFragment mMasterListFragment;

    /**
     * Sets up the UI and variables.
     *
     * @param savedInstanceState    contains the stored variables of the state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RECIPE_KEY)) {
            mRecipe = intent.getParcelableExtra(RECIPE_KEY);

            setTitle(mRecipe.getName());

            mMasterListFragment = (RecipeMasterListFragment) getSupportFragmentManager()
                    .findFragmentByTag(RecipeMasterListFragment.class.getCanonicalName());
            if (mMasterListFragment == null) {
                mMasterListFragment = new RecipeMasterListFragment();
                mMasterListFragment.setRecipe(mRecipe);
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.master_list_container, mMasterListFragment,
                            RecipeMasterListFragment.class.getCanonicalName())
                    .commit();
        }
    }

    /**
     * When a step was clicked upon, the new activity for this specific step will be opened
     * with the correct parameters.
     *
     * @param position  the position in the list which was clicked on.
     *                  0 is the ingredients, the others are recipe steps.
     * @param steps     the steps for the recipe.
     */
    @Override
    public void onStepClick(int position, Step[] steps) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            Step[] mySteps = steps;
            // position 0 are the ingredients which aren't in the stepList, so position 1 will
            //   become position 0 and so forth.
            int pos = position - 1;
            // If the click is on the ingredients:
            if (position == 0) {
                mySteps = new Step[] {new Step(
                        EntityUtil.getAllIngredientsAsAnEnumerationString(mRecipe.getIngredients())
                )};
                pos = position;
            }

            RecipeMasterDetailFragment masterDetailFragment = new RecipeMasterDetailFragment();
            masterDetailFragment.setData(mySteps, pos);

            // Change the fragment to display the newly selected recipe step.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.master_detail_container, masterDetailFragment)
                    .commit();
        } else {
            ArrayList<Step> stepList = new ArrayList<>();
            // position 0 are the ingredients which aren't in the stepList, so position 1 will
            //   become position 0 and so forth.
            int pos = position - 1;
            // If the click is on the ingredients:
            if (position == 0) {
                stepList.add(new Step(
                        EntityUtil.getAllIngredientsAsAnEnumerationString(mRecipe.getIngredients())
                ));
                pos = position;
            } else {
                stepList.addAll(Arrays.asList(steps));
            }

            // Open the new activity for the specific recipe step.
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(RECIPE_STEP_POS_KEY, pos);
            intent.putParcelableArrayListExtra(RECIPE_STEPS_KEY, stepList);

            startActivity(intent);
        }
    }
}
