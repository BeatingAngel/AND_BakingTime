package com.goldencrow.android.bakingtime;

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

import com.goldencrow.android.bakingtime.entities.Recipe;

import static com.goldencrow.android.bakingtime.RecipeDetailActivity.RECIPE_KEY;

/**
 * Created by Philipp
 */

public class RecipeMasterListFragment extends Fragment {

    private RecipeStepAdapter mAdapter;

    ImageView mLoadingIndicatorIv;
    RecyclerView mRecipeStepsRv;

    private Recipe mRecipe;

    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_master_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadingIndicatorIv = view.findViewById(R.id.loading_indicator_iv);
        mRecipeStepsRv = view.findViewById(R.id.recipe_step_list_rv);

        if (mRecipe == null && savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(RecipeDetailActivity.RECIPE_KEY);
        }

        // make the donut symbol rotate like a loading indicator
        addSpinningEffectToLoader();

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);

        mAdapter = new RecipeStepAdapter();

        mRecipeStepsRv.setLayoutManager(layoutManager);
        mRecipeStepsRv.setAdapter(mAdapter);
        mRecipeStepsRv.setHasFixedSize(true);

        removeLoadingIndicator();
        mAdapter.setSteps(mRecipe.getSteps());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RecipeDetailActivity.RECIPE_KEY, mRecipe);
    }

    private void addSpinningEffectToLoader() {
        RotateAnimation rotate = new RotateAnimation(
                0, 180,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        mLoadingIndicatorIv.startAnimation(rotate);
    }

    private void removeLoadingIndicator() {
        mLoadingIndicatorIv.clearAnimation();
        mLoadingIndicatorIv.setVisibility(View.INVISIBLE);
    }
}
