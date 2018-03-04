package com.goldencrow.android.bakingtime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.utils.EntityUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by Philipp
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context mContext;
    private Recipe[] mRecipes;

    public RecipeAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card_layout, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipes[position];

        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            Picasso.with(mContext)
                    .load(recipe.getImage())
                    .into(holder.mBackgroundImage);
        }
        holder.mTitleTv.setText(recipe.getName());
        holder.mServingsTv.setText(String.valueOf(recipe.getServings()));
        holder.mStepsTv.setText(String.valueOf(recipe.getSteps().length));
        holder.mIngredientsTv.setText(
                EntityUtil.getAllIngredientsAsOneString(recipe.getIngredients()));

        holder.mBackCardTitleTv.setText(recipe.getName());
        holder.mBackCardIngredientsTv.setText(
                EntityUtil.getAllIngredientsAsAnEnumerationString(recipe.getIngredients()));
    }

    @Override
    public int getItemCount() {
        return mRecipes != null ? mRecipes.length : 0;
    }

    public void setRecipes(Recipe[] recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout front_card_layout;
        ConstraintLayout back_card_layout;

        ImageView mBackgroundImage;
        TextView mTitleTv;
        TextView mServingsTv;
        TextView mStepsTv;
        TextView mIngredientsTv;

        TextView mBackCardTitleTv;
        TextView mBackCardIngredientsTv;

        RecipeViewHolder(View view) {
            super(view);

            front_card_layout = view.findViewById(R.id.front_card_layout);
            back_card_layout = view.findViewById(R.id.back_card_layout);

            mBackgroundImage = view.findViewById(R.id.card_background_image);
            mTitleTv = view.findViewById(R.id.card_title_tv);
            mServingsTv = view.findViewById(R.id.servings_tv);
            mStepsTv = view.findViewById(R.id.steps_tv);
            mIngredientsTv = view.findViewById(R.id.ingredients_tv);

            mBackCardTitleTv = view.findViewById(R.id.back_card_title_tv);
            mBackCardIngredientsTv = view.findViewById(R.id.back_card_ingredients_tv);

            // code from https://stackoverflow.com/a/46111960
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
                    oa1.setInterpolator(new DecelerateInterpolator());
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            if (front_card_layout.getVisibility() != View.INVISIBLE) {
                                front_card_layout.setVisibility(View.INVISIBLE);
                                back_card_layout.setVisibility(View.VISIBLE);
                            } else {
                                front_card_layout.setVisibility(View.VISIBLE);
                                back_card_layout.setVisibility(View.INVISIBLE);
                            }

                            oa2.start();
                        }
                    });
                    oa1.setDuration(500);
                    oa2.setDuration(500);
                    oa1.start();
                }
            });
        }
    }

}
