package com.goldencrow.android.bakingtime;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.goldencrow.android.bakingtime.recyclerview.TestUtils.withRecyclerView;
import static org.hamcrest.CoreMatchers.is;

/**
 * Tests the UI of the MainActivity.
 *
 * @author Philipp Hermüller
 * @version 2018.03.24
 * @since 1.0
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    /**
     * Name of the tested recipe.
     */
    private static final String RECIPE_NAME = "Brownies";

    /**
     * Image shown for the tested recipe.
     */
    private static final int BACKGROUND_IMAGE = R.drawable.bakings;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    /**
     * Tests if the content of the recipe card is set correctly.
     */
    @Test
    public void checkRecipeCardContent() {
        onView(withRecyclerView(R.id.recipe_list_rv)
                .atPositionOnView(1, R.id.card_title_tv))
                .check(matches(withText(RECIPE_NAME)));

        onView(withRecyclerView(R.id.recipe_list_rv)
                .atPositionOnView(1, R.id.card_background_image))
                .check(matches(withTagValue(is((Object) BACKGROUND_IMAGE))));
    }

    /**
     * Tests if a click on a recipe card opens the detail activity for this recipe.
     */
    @Test
    public void clickRecipeCard_OpensRecipeDetailActivity() {
        onView(withId(R.id.recipe_list_rv)).perform(
                RecyclerViewActions.actionOnItemAtPosition(
                        0, click()));

        onView(withId(R.id.recipe_step_list_rv)).check(matches(isDisplayed()));
    }


}
