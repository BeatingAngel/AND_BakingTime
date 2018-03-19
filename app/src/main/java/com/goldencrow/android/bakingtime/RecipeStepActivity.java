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
     * The Debug-Tag used for logging errors, warning, .... in this class.
     */
    private final String TAG = this.getClass().getSimpleName();

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
     * The UI Element which displays a video, thumbnail or image additionally to the recipe step
     * description as visual help.
     */
    SimpleExoPlayerView mExoPlayerView;

    /**
     * The Exo Player handles the video for the mExoPlayerView.
     */
    SimpleExoPlayer mExoPlayer;

    /**
     * The LinearLayout changes orientation if no video or thumbnail are available.
     * The reason for this is the size of the default image which will be displayed instead.
     */
    LinearLayout mLinearLayout;

    /**
     * Contains the recipe step description.
     */
    TextView mStepDescriptionTv;

    /**
     * Navigates to the previous recipe step if there is one.
     */
    Button mPreviousBtn;

    /**
     * Navigates to the next recipe step if there is one.
     */
    Button mNextBtn;

    /**
     * Contains all recipe steps for the navigation.
     */
    ArrayList<Step> mStepsList;

    /**
     * Contains the current position which is displayed from the array of recipe steps.
     */
    int mPos;

    /**
     * Sets up the UI and initializes the variables.
     *
     * @param savedInstanceState    contains the saved variables over state changes.
     *                              In this case, the array of recipe steps and the current position.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recipe_master_detail);

        mStepDescriptionTv = findViewById(R.id.step_desc_tv);
        mExoPlayerView = findViewById(R.id.video_player);
        mLinearLayout = findViewById(R.id.linear_layout);
        mPreviousBtn = findViewById(R.id.prev_btn);
        mNextBtn = findViewById(R.id.next_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RECIPE_STEPS_KEY)) {
            mStepsList = intent.getParcelableArrayListExtra(RECIPE_STEPS_KEY);
            mPos = intent.getIntExtra(RECIPE_STEP_POS_KEY, 0);

            initializePlayer();
            updateUI();

            mPreviousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPos > 0) {
                        mPos--;
                        launchNewStepIntent();
                    }
                }
            });
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPos < mStepsList.size() - 1) {
                        mPos++;
                        launchNewStepIntent();
                    }
                }
            });
        }
    }

    /**
     * If the fragment is deleted, then clean up the ExoPlayer.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        releasePlayer();
    }

    private void launchNewStepIntent() {
        mExoPlayer.stop();
        Intent nextStepIntent = new Intent(
                RecipeStepActivity.this, RecipeStepActivity.class);
        nextStepIntent.putExtra(RECIPE_STEP_POS_KEY, mPos);
        nextStepIntent.putParcelableArrayListExtra(RECIPE_STEPS_KEY, mStepsList);
        finish();
        startActivity(nextStepIntent);
    }

    /**
     * Updates the UI so that all the information from #setData() will now be displayed.
     */
    private void updateUI() {
        int ingredientsPosition = 0;
        Step step = mStepsList.get(mPos);

        mStepDescriptionTv.setText(step.getDescription());
        if (mPos == ingredientsPosition) {
            mPreviousBtn.setVisibility(View.INVISIBLE);
        }
        if (mPos == mStepsList.size() - 1) {
            mNextBtn.setVisibility(View.INVISIBLE);
        }

        // If there is a video, display it!
        if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            updatePlayer(Uri.parse(step.getVideoURL()));
        } else {
            RequestCreator requestCreator;
            // If there is a thumbnail, display it!
            if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()) {
                requestCreator = Picasso.with(this).load(step.getThumbnailURL());
            } else { // If there is nothing, display the default image of a cook.
                Random random = new Random();
                int r = random.nextInt() % 2;
                int resourceId = r == 0 ? R.drawable.cook_01 : R.drawable.cook_02;
                requestCreator = Picasso.with(this).load(resourceId);

                ViewGroup.LayoutParams params = mExoPlayerView.getLayoutParams();
                params.height = (int) getResources()
                        .getDimension(R.dimen.exoplayer_default_pic_phone_height);
                mExoPlayerView.setLayoutParams(params);
            }
            mExoPlayerView.setUseController(false);

            // Display the image from the web. If an error occurred, log it!
            requestCreator
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mExoPlayerView.setDefaultArtwork(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.w(TAG, "Image could not be converted to Bitmap.");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        }
    }

    /**
     * Sets up the default parameters for the ExoPlayer.
     */
    private void initializePlayer() {
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        mExoPlayerView.setPlayer(mExoPlayer);
    }

    /**
     * Updates the ExoPlayer to display the new Uri in the ExoPlayerView.
     *
     * @param uri   the uri to the resource which will be displayed.
     */
    private void updatePlayer(Uri uri) {
        String userAgent = Util.getUserAgent(this, "BakingTime");
        MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                this, userAgent), new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * cleans up the ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }
}
