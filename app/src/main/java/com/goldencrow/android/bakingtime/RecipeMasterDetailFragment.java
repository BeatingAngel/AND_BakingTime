package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

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
    private static final String STEPS_KEY = "recipe_steps_key";

    /**
     * Key used to store the current position of the step-list.
     */
    private static final String POS_KEY = "pos_in_steps_key";

    /**
     * Key to the stored position of the video.
     */
    private static final String VID_POS_KEY = "video_position_key";

    /**
     * Key to the stored variables of the state of the video.
     */
    private static final String VID_STATE_KEY = "video_state_key";

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
     * The position in which the player from the video is currently located at (minute).
     */
    Long mVideoPosition;

    /**
     * The state in which the player is in (PLAY, PAUSE, STOPPED,...)
     */
    Integer mVideoState;

    /**
     * is TRUE if the current device is a tablet and FALSE if it is a phone.
     */
    boolean mIsTablet = false;

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

        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        int orientation = getResources().getConfiguration().orientation;

        mStepDescriptionTv = view.findViewById(R.id.step_desc_tv);
        mExoPlayerView = view.findViewById(R.id.video_player);
        mLinearLayout = view.findViewById(R.id.linear_layout);
        mPreviousBtn = view.findViewById(R.id.prev_btn);
        mNextBtn = view.findViewById(R.id.next_btn);

        // Set up the Previous- and Next-Navigation-Buttons.
        if (mIsTablet || orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPreviousBtn.setVisibility(View.GONE);
            mNextBtn.setVisibility(View.GONE);

            Guideline guideline = view.findViewById(R.id.text_to_control_divider);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
            params.guidePercent = 1;
            guideline.setLayoutParams(params);

            if (orientation == Configuration.ORIENTATION_LANDSCAPE && !mIsTablet) {
                ViewGroup.LayoutParams videoParams = mExoPlayerView.getLayoutParams();
                videoParams.height = MATCH_PARENT;
                mExoPlayerView.setLayoutParams(videoParams);
            }
        } else {
            mPreviousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPos > 0) {
                        mPos--;
                        changeDetailFragment();
                    }
                }
            });
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPos < mSteps.length - 1) {
                        mPos++;
                        changeDetailFragment();
                    }
                }
            });
        }

        // TRUE if the orientation changed. See #onSaveInstanceState()
        if (savedInstanceState != null) {
            mSteps = (Step[]) savedInstanceState.getParcelableArray(STEPS_KEY);
            mPos = savedInstanceState.getInt(POS_KEY);
            mVideoPosition = savedInstanceState.getLong(VID_POS_KEY);
            mVideoState = savedInstanceState.getInt(VID_STATE_KEY);
        }

        if (mExoPlayer == null) {
            initializePlayer();
            updateUI();
        } else if (mVideoPosition != null && mVideoState != null) {
            mExoPlayer.seekTo(mVideoPosition);
            if (mVideoState == PlaybackState.STATE_PAUSED || mVideoState == PlaybackState.STATE_STOPPED) {
                mExoPlayer.setPlayWhenReady(false);
            } else {
                mExoPlayer.setPlayWhenReady(true);
            }
        }

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
        outState.putLong(VID_POS_KEY, mExoPlayer.getCurrentPosition());
        outState.putInt(VID_STATE_KEY, mExoPlayer.getPlaybackState());
    }

    /**
     * changes the currently displayed Detail Fragment to the one which is selected through the
     * navigation buttons.
     *
     * @see #onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    private void changeDetailFragment() {
        RecipeMasterDetailFragment masterDetailFragment = new RecipeMasterDetailFragment();
        masterDetailFragment.setData(mSteps, mPos);

        ((RecipeStepActivity) getActivity()).changeDetailFragment(masterDetailFragment);
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
     * Returns the position in the step-Array which is currently displayed.
     *
     * @return  the position in the step-Array.
     */
    public int getPos() {
        return mPos;
    }

    /**
     * Updates the UI so that all the information from #setData() will now be displayed.
     */
    public void updateUI() {
        Step step = mSteps[mPos];

        mStepDescriptionTv.setText(step.getDescription());
        if (!mIsTablet) {
            if (mPos == 0) {
                mPreviousBtn.setVisibility(View.INVISIBLE);
            }
            if (mPos == mSteps.length - 1) {
                mNextBtn.setVisibility(View.INVISIBLE);
            }
        }

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

                if (mIsTablet) {
                    mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    mExoPlayerView.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                } else {
                    ViewGroup.LayoutParams params = mExoPlayerView.getLayoutParams();
                    params.height = (int) getResources()
                            .getDimension(R.dimen.exoplayer_default_pic_phone_height);
                    mExoPlayerView.setLayoutParams(params);
                }
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

        boolean playWhenReady = true;
        if (mVideoPosition != null && mVideoState != null) {
            mExoPlayer.seekTo(mVideoPosition);
            if (mVideoState == PlaybackState.STATE_PAUSED || mVideoState == PlaybackState.STATE_STOPPED) {
                playWhenReady = false;
            }
        }
        mExoPlayer.setPlayWhenReady(playWhenReady);
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
