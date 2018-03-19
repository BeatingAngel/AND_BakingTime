package com.goldencrow.android.bakingtime;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.goldencrow.android.bakingtime.adapters.RecipeStepAdapter;
import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.entities.Step;

/**
 * Displays the list of steps of the selected recipe.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class RecipeMasterListFragment extends Fragment {

    /**
     * The callback method which the parent activity should have so that it can handle the click
     * on a step which happens in here.
     */
    private OnStepClickListener mCallback;

    /**
     * This interface has to be implemented by the parent activity of this fragment.
     */
    public interface OnStepClickListener {
        /**
         * @see RecipeDetailActivity#onStepClick(int, Step[])
         */
        void onStepClick(int position, Step[] steps);
    }

    /**
     * Displays a loading indicator for visual help, so that the user doesn't think the app froze
     * if the web request takes too long.
     */
    ImageView mLoadingIndicatorIv;

    /**
     * The list for the recipe steps.
     */
    RecyclerView mRecipeStepsRv;

    /**
     * The selected recipe which contains all the recipe steps and other information.
     */
    private Recipe mRecipe;

    /**
     *
     *
     * @param inflater              inflates the view into the UI.
     * @param container             the container which will hold this fragment.
     * @param savedInstanceState    contains the saved variables over state changes.
     *                              In this case the recipe.
     * @return                      the newly inflated/created View.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_master_list,
                container, false);

        mLoadingIndicatorIv = rootView.findViewById(R.id.loading_indicator_iv);
        mRecipeStepsRv = rootView.findViewById(R.id.recipe_step_list_rv);

        if (mRecipe == null && savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(RecipeDetailActivity.RECIPE_KEY);
        }

        // make the donut symbol rotate like a loading indicator
        addSpinningEffectToLoader();

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);

        RecipeStepAdapter adapter = new RecipeStepAdapter(getContext(), mCallback);

        mRecipeStepsRv.setLayoutManager(layoutManager);
        mRecipeStepsRv.setAdapter(adapter);
        mRecipeStepsRv.setHasFixedSize(true);

        removeLoadingIndicator();
        adapter.setSteps(mRecipe.getSteps());

        return rootView;
    }

    /**
     * If the orientation changes, save the important variables to recreate the current environment
     * after the orientation change.
     *
     * @param outState  contains the recipe which shall be remembered after the state change.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RecipeDetailActivity.RECIPE_KEY, mRecipe);
    }

    /**
     * Checks if the parent activity has implemented the OnStepClickListener correctly.
     *
     * @param context   the context of the parent activity which should also have the
     *                  OnStepClickListener implemented.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener.");
        }
    }

    /**
     * Set the currently selected recipe.
     *
     * @param recipe    the selected recipe.
     *
     * @see RecipeDetailActivity#onCreate(Bundle)
     */
    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }

    /**
     * Adds the spinning animation to the loading indicator (donut).
     */
    private void addSpinningEffectToLoader() {
        // the donut rotates 180 degrees.
        RotateAnimation rotate = new RotateAnimation(
                0, 180,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // the rotation takes one second.
        rotate.setDuration(1000);
        // and this rotation is repeated for infinity.
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        mLoadingIndicatorIv.startAnimation(rotate);
    }

    /**
     * removes the animation from the loading indicator and hides it.
     */
    private void removeLoadingIndicator() {
        mLoadingIndicatorIv.clearAnimation();
        mLoadingIndicatorIv.setVisibility(View.INVISIBLE);
    }
}
