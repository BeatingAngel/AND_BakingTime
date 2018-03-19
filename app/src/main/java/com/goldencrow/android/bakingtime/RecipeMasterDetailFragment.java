package com.goldencrow.android.bakingtime;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.Random;

/**
 * Displays the details of a recipe step.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class RecipeMasterDetailFragment extends Fragment {

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
    private static final String STEPS_KEY = "steps";

    /**
     * Key used to store the current position of the step-list.
     */
    private static final String POS_KEY = "pos";

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
     * Contains the recipe step description.
     */
    TextView mStepDescriptionTv;

    /**
     * The LinearLayout changes orientation if no video or thumbnail are available.
     * The reason for this is the size of the default image which will be displayed instead.
     */
    LinearLayout mLinearLayout;

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
    Step[] mSteps;

    /**
     * Contains the current position which is displayed from the array of recipe steps.
     */
    int mPos;

    /**
     * Sets up the UI and initializes the variables.
     *
     * @param inflater              inflates the view into the UI.
     * @param container             the container which will hold this fragment.
     * @param savedInstanceState    contains the saved variables over state changes.
     *                              In this case, the array of recipe steps and the current position.
     * @return                      the newly inflated/created View.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_master_detail, container, false);

        mStepDescriptionTv = view.findViewById(R.id.step_desc_tv);
        mExoPlayerView = view.findViewById(R.id.video_player);
        mLinearLayout = view.findViewById(R.id.linear_layout);
        mPreviousBtn = view.findViewById(R.id.prev_btn);
        mNextBtn = view.findViewById(R.id.next_btn);

        mPreviousBtn.setVisibility(View.INVISIBLE);
        mNextBtn.setVisibility(View.INVISIBLE);

        // only TRUE if the orientation changed. See #onSaveInstanceState()
        if (savedInstanceState != null) {
            mSteps = (Step[]) savedInstanceState.getParcelableArray(STEPS_KEY);
            mPos = savedInstanceState.getInt(POS_KEY);
        }

        initializePlayer();
        updateUI();

        return view;
    }

    /**
     * If the fragment is deleted, then clean up the ExoPlayer.
     */
    @Override
    public void onDetach() {
        super.onDetach();

        releasePlayer();
    }

    /**
     * If the orientation changes, save the important variables to recreate the current environment
     * after the orientation change.
     *
     * @param outState  contains the variables which shall be remembered.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray(STEPS_KEY, mSteps);
        outState.putInt(POS_KEY, mPos);
    }

    /**
     * Initializes the variables which hold information of the recipe.
     *
     * @param steps the array of recipe steps in this recipe.
     * @param pos   the current position in the array which is displayed.
     */
    public void setData(Step[] steps, int pos) {
        mSteps = steps;
        mPos = pos;
    }

    /**
     * Updates the UI so that all the information from #setData() will now be displayed.
     */
    public void updateUI() {
        Step step = mSteps[mPos];

        mStepDescriptionTv.setText(step.getDescription());

        // If there is a video, display it!
        if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            updatePlayer(Uri.parse(step.getVideoURL()));
        } else {
            RequestCreator requestCreator;
            // If there is a thumbnail, display it!
            if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()) {
                requestCreator = Picasso.with(getContext()).load(step.getThumbnailURL());
            } else { // If there is nothing, display the default image of a cook.
                Random random = new Random();
                int r = random.nextInt() % 2;
                int resourceId = r == 0 ? R.drawable.cook_01 : R.drawable.cook_02;
                requestCreator = Picasso.with(getContext()).load(resourceId);
                mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                mExoPlayerView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
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
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        mExoPlayerView.setPlayer(mExoPlayer);
    }

    /**
     * Updates the ExoPlayer to display the new Uri in the ExoPlayerView.
     *
     * @param uri   the uri to the resource which will be displayed.
     */
    private void updatePlayer(Uri uri) {
        String userAgent = Util.getUserAgent(getContext(), "BakingTime");
        MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                getContext(), userAgent),
                new DefaultExtractorsFactory(), null, null);
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
