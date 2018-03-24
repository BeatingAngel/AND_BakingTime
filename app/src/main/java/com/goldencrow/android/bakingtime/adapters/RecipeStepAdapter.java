package com.goldencrow.android.bakingtime.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldencrow.android.bakingtime.R;
import com.goldencrow.android.bakingtime.RecipeMasterListFragment;
import com.goldencrow.android.bakingtime.entities.Step;

import java.util.Arrays;

/**
 * The adapter which handles all the recipe steps and a step showing the ingredients.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    /**
     * Value defining the view as the item which will display the ingredients
     * instead of a step.
     */
    private static final int INGREDIENTS_VIEW_TYPE = 0;

    /**
     * Value defining the view as the item which will display a recipe step.
     */
    private static final int RECIPE_STEP_VIEW_TYPE = 1;

    /**
     * Context of the activity which initializes the RecyclerList.
     */
    private Context mContext;

    /**
     * Contains all steps which will be displayed in the list.
     * It will be initialized by an intern method.
     *
     * @see #setSteps(Step[])
     */
    private Step[] mSteps;

    /**
     * This callback is used to call the onClick method from the parent activity.
     *
     * @see RecipeStepViewHolder#onClick(View)
     */
    private RecipeMasterListFragment.OnStepClickListener mCallback;

    /**
     * Contains the selected step from the list.
     * <p>
     * This is used so that if another step (item in list) is selected, the old selected item
     * can be set back to normal.
     */
    private RecipeStepViewHolder mSelectedStep;

    /**
     * Constructor which initializes the context and callback variables.
     *
     * @param context   the context of the parent activity where the RecyclerList is located at.
     * @param callback  the callback method containing the method which will be performed if a
     *                  step-item was clicked on.
     */
    public RecipeStepAdapter(Context context, RecipeMasterListFragment.OnStepClickListener callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    /**
     * This method inflates the appropriate layout for the list-item.
     * <p>
     * If the item is first (0) in position then the item will be inflated as an ingredient layout.
     * Otherwise it will be inflated as a step layout.
     *
     * @param parent    the parent-viewGroup which owns this view.
     * @param viewType  specifies which layout the view on this position should have.
     * @return          a new RecipeStepViewHolder object.
     */
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

    /**
     * This method sets the information from the recipe-step at the current position into the
     * appropriate view and therefore fills them with text and info.
     * <p>
     * But if the view is of the IngredientViewType, then no information will be bound.
     *
     * @param holder    the ViewHolder where all the views are located.
     * @param position  the position of the item in the list.
     */
    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {
        int ingredientsAndIntroSpace = 1;

        if (holder.mViewType == RECIPE_STEP_VIEW_TYPE) {
            Step step = mSteps[position - 1];
            holder.mStepDescTv.setText(step.getShortDescription());
            if (position > 1) {
                String stepCount = Integer.toString(position - ingredientsAndIntroSpace);
                holder.mStepBadgeTv.setText(stepCount);
            } else {
                holder.mStepBadgeTv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Tells how many items the adapter currently holds.
     *
     * @return  the number of items in the list.
     */
    @Override
    public int getItemCount() {
        int space_for_ingredient_card = 1;

        if (mSteps != null) {
            return mSteps.length + space_for_ingredient_card;
        }
        return 0;
    }

    /**
     * Return the appropriate viewType for the item at the passed position.
     *
     * @param position  the current position of the item in the list.
     * @return          the viewType of the item.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return INGREDIENTS_VIEW_TYPE;
        } else {
            return RECIPE_STEP_VIEW_TYPE;
        }
    }

    /**
     * Initializes the Steps for the list.
     *
     * @param steps the array of steps which will be displayed in the list.
     */
    public void setSteps(Step[] steps) {
        Arrays.sort(steps);
        this.mSteps = steps;
        notifyDataSetChanged();
    }

    /**
     * The class representing an item in the list.
     *
     * @author Philipp Hermüller
     * @version 2018.3.14
     * @since 1.0
     */
    class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int mViewType;

        TextView mStepBadgeTv;
        TextView mStepDescTv;

        /**
         * Constructor which initializes the view fields and the onClickListener.
         *
         * @param itemView  the view representing the item in the list.
         * @param viewType  the viewType which tells what type of view it is.
         */
        RecipeStepViewHolder(View itemView, int viewType) {
            super(itemView);

            this.mViewType = viewType;

            mStepBadgeTv = itemView.findViewById(R.id.step_badge_tv);
            mStepDescTv = itemView.findViewById(R.id.recipe_step_desc_tv);

            itemView.setOnClickListener(this);
        }

        /**
         * Handles the click on a recipe step (and not the ingredients).
         *
         * @param view  the view which was clicked on.
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            // If the item is not on the position where the ingredients-card is, then
            //    color the item as selected.
            if (position != 0) {
                if (mSelectedStep != null) {
                    mSelectedStep.mStepDescTv
                            .setBackgroundColor(mContext.getColor(R.color.transparent));
                }
                mStepDescTv.setBackgroundColor(mContext.getColor(R.color.listItemSelected));
                mSelectedStep = this;
            }

            mCallback.onStepClick(position, mSteps);
        }
    }
}
