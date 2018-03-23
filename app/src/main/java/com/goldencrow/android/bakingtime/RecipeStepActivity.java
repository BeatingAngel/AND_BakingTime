package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldencrow.android.bakingtime.entities.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Random;

/**
 * The recipe step detail view for the phone.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class RecipeStepActivity extends AppCompatActivity {

    /**
     * Key used to store the array of recipe steps in.
     * <p>
     * The reason why all steps are sent is that so that the navigation to the next and previous
     * step works.
     */
    public static final String RECIPE_STEPS_KEY = "STEP_KEY";

    /**
     * Key used to store the current position of the step-list.
     */
    public static final String RECIPE_STEP_POS_KEY = "STEP_POS_KEY";

    /**
     * Displays the details from a recipe step as a fragment.
     */
    RecipeMasterDetailFragment mMasterDetailFragment;

    /**
     * Sets up the UI and initializes the variables.
     *
     * @param savedInstanceState    contains the saved variables over state changes.
     *                              In this case, the array of recipe steps and the current position.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RECIPE_STEPS_KEY)) {
            ArrayList<Step> stepsList = intent.getParcelableArrayListExtra(RECIPE_STEPS_KEY);
            int pos = intent.getIntExtra(RECIPE_STEP_POS_KEY, 0);
            if (savedInstanceState != null) {
                pos = savedInstanceState.getInt(RECIPE_STEP_POS_KEY);
            }

            mMasterDetailFragment = new RecipeMasterDetailFragment();
            Step[] stepArr = new Step[stepsList.size()];
            stepArr = stepsList.toArray(stepArr);
            mMasterDetailFragment.setData(stepArr, pos);

            changeDetailFragment(mMasterDetailFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(RECIPE_STEP_POS_KEY, mMasterDetailFragment.getPos());
    }

    /**
     * Changes the current detail fragment to the previous/next one depending on which navigation
     * button was clicked upon.
     *
     * @param recipeMasterDetailFragment    the newly displayed detail fragment.
     */
    public void changeDetailFragment(RecipeMasterDetailFragment recipeMasterDetailFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.master_detail_container, recipeMasterDetailFragment)
                .commit();
    }
}
