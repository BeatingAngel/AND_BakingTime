package com.goldencrow.android.bakingtime.adapters;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldencrow.android.bakingtime.R;
import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.utils.EntityUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by Philipp
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context mContext;
    private Recipe[] mRecipes;

    private ImageView mBookmarkedView;

    private RecipeOnClickListener mRecipeClick;

    public interface RecipeOnClickListener {
        void OnClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, RecipeOnClickListener recipeClick) {
        this.mContext = context;
        this.mRecipeClick = recipeClick;
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
        } else {
            holder.mErrorTv.setVisibility(View.VISIBLE);
        }
        holder.mTitleTv.setText(recipe.getName());
        holder.mServingsTv.setText(String.valueOf(recipe.getServings()));
        holder.mStepsTv.setText(String.valueOf(recipe.getSteps().length));

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
        ImageView mBookmarkIv;
        TextView mErrorTv;
        TextView mTitleTv;
        TextView mServingsTv;
        TextView mStepsTv;
        Button mIngredientsBtn;

        TextView mBackCardTitleTv;
        TextView mBackCardIngredientsTv;

        RecipeViewHolder(final View cardView) {
            super(cardView);

            front_card_layout = cardView.findViewById(R.id.front_card_layout);
            back_card_layout = cardView.findViewById(R.id.back_card_layout);

            mBackgroundImage = cardView.findViewById(R.id.card_background_image);
            mBookmarkIv = cardView.findViewById(R.id.bookmark_iv);
            mErrorTv = cardView.findViewById(R.id.error_tv);
            mTitleTv = cardView.findViewById(R.id.card_title_tv);
            mServingsTv = cardView.findViewById(R.id.servings_tv);
            mStepsTv = cardView.findViewById(R.id.steps_tv);
            mIngredientsBtn = cardView.findViewById(R.id.ingredients_btn);

            mBackCardTitleTv = cardView.findViewById(R.id.back_card_title_tv);
            mBackCardIngredientsTv = cardView.findViewById(R.id.back_card_ingredients_tv);

            mBookmarkIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mBookmarkIv.getHeight() != 120) {
                        EntityUtil.setFavoriteRecipe(mContext,
                                mTitleTv.getText().toString(),
                                mBackCardIngredientsTv.getText().toString());
                    }
                    toggleImageHeight(mBookmarkIv);
                    if (mBookmarkedView != null) {
                        toggleImageHeight(mBookmarkedView);
                    }
                    mBookmarkedView = mBookmarkIv;
                }
            });

            //========
            // Handle the card-flip animations:
            //========
            // code from https://stackoverflow.com/a/46111960
            View.OnClickListener cardRotateClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0f);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cardView, "scaleX", 0f, 1f);
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
            };
            mIngredientsBtn.setOnClickListener(cardRotateClick);
            back_card_layout.setOnClickListener(cardRotateClick);
            front_card_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mRecipeClick.OnClick(mRecipes[position]);
                }
            });
        }

        private void toggleImageHeight(ImageView view) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = view.getHeight() != 120 ? 120 : 50;
            view.setLayoutParams(params);
        }
    }

}
