package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.goldencrow.android.bakingtime.entities.Ingredient;
import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.entities.Step;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.goldencrow.android.bakingtime.recyclerview.TestUtils.withRecyclerView;
import static org.hamcrest.CoreMatchers.not;

/**
 * Tests the UI of the DetailActivity.
 * <p>
 * The content of the UI may differ based on the device used. The reason for this is that
 * a tablet has a master detail flow with fragments and those fragments are split up in the
 * phone view.
 *
 * @author Philipp Hermüller
 * @version 2018.03.24
 * @since 1.0
 */
@RunWith(AndroidJUnit4.class)
public class DetailActivityScreenTest {

    private static Recipe mRecipe;

    @Rule
    public ActivityTestRule<RecipeDetailActivity> mRecipeDetailActivityTestRule
            = new ActivityTestRule<RecipeDetailActivity>(RecipeDetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Step[] steps = new Step[] {
                    new Step(0,
                            "Intro",
                            "Intro to recipe",
                            "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4",
                            ""),
                    new Step(1,
                            "Start",
                            "Start to bake",
                            "",
                            "")
            };

            mRecipe = new Recipe(
                    1,
                    "Brownies",
                    new Ingredient[]{},
                    steps,
                    8,
                    "");

            Intent intent = new Intent();
            intent.putExtra(RecipeDetailActivity.RECIPE_KEY, mRecipe);

            return intent;
        }
    };

    /**
     * Tests if the RecipeDetailMasterList is initialized correctly.
     */
    @Test
    public void checkMasterList() {
        // check if the first item is the ingredients card.
        onView(withRecyclerView(R.id.recipe_step_list_rv)
                .atPositionOnView(0, R.id.ingredients_step_tv))
                .check(matches(withText(R.string.ingredients)));

        // check if the first step is a intro and has no badge
        onView(withRecyclerView(R.id.recipe_step_list_rv)
                .atPositionOnView(1, R.id.recipe_step_desc_tv))
                .check(matches(withText(mRecipe.getSteps()[0].getShortDescription())));
        onView(withRecyclerView(R.id.recipe_step_list_rv)
                .atPositionOnView(1, R.id.step_badge_tv))
                .check(matches(not(isDisplayed())));

        // check that the second step is a real step and has a badge with the number 1
        onView(withRecyclerView(R.id.recipe_step_list_rv)
                .atPositionOnView(2, R.id.recipe_step_desc_tv))
                .check(matches(withText(mRecipe.getSteps()[1].getShortDescription())));
        onView(withRecyclerView(R.id.recipe_step_list_rv)
                .atPositionOnView(2, R.id.step_badge_tv))
                .check(matches(isDisplayed()));
        onView(withRecyclerView(R.id.recipe_step_list_rv)
                .atPositionOnView(2, R.id.step_badge_tv))
                .check(matches(withText("1")));
    }

}
