package com.goldencrow.android.bakingtime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldencrow.android.bakingtime.entities.Step;

import java.util.Arrays;

/**
 * Created by Philipp
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    private static final int INGREDIENTS_VIEW_TYPE = 0;
    private static final int RECIPE_STEP_VIEW_TYPE = 1;

    private Step[] mSteps;

    RecipeStepAdapter() {
    }

    void setSteps(Step[] steps) {
        Arrays.sort(steps);
        this.mSteps = steps;
        notifyDataSetChanged();
    }

    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == INGREDIENTS_VIEW_TYPE) {
            layoutId = R.layout.recipe_ingredient_card_layout;
        } else {
            layoutId = R.layout.recipe_step_layout;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);

        return new RecipeStepViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {

        if (holder.mViewType == RECIPE_STEP_VIEW_TYPE) {
            Step step = mSteps[position - 1];
            holder.mStepDescTv.setText(step.getShortDescription());
        }
    }

    @Override
    public int getItemCount() {
        return mSteps != null ? mSteps.length : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return INGREDIENTS_VIEW_TYPE;
        } else {
            return RECIPE_STEP_VIEW_TYPE;
        }
    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder {

        int mViewType;

        TextView mStepDescTv;

        RecipeStepViewHolder(View itemView, int viewType) {
            super(itemView);

            this.mViewType = viewType;

            mStepDescTv = itemView.findViewById(R.id.recipe_step_desc_tv);
        }

    }
}
