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
 * Created by Philipp
 */

public class RecipeMasterListFragment extends Fragment {

    private OnStepClickListener mCallback;

    public interface OnStepClickListener {
        void onStepClick(int position, Step[] steps);
    }

    ImageView mLoadingIndicatorIv;
    RecyclerView mRecipeStepsRv;

    private Recipe mRecipe;

    public void setRecipe(Recipe recipe) {
        this.mRecipe = recipe;
    }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RecipeDetailActivity.RECIPE_KEY, mRecipe);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener.");
        }
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
