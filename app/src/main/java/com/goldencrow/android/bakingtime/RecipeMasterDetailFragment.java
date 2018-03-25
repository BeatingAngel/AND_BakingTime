package com.goldencrow.android.bakingtime;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.goldencrow.android.bakingtime.entities.Step;
import com.google.android.exoplayer2.C;
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

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.video_player)
    SimpleExoPlayerView mExoPlayerView;

    /**
     * The Exo Player handles the video for the mExoPlayerView.
     */
    SimpleExoPlayer mExoPlayer;

    /**
     * Contains the recipe step description in the layout where the video is located at.
     */
    @BindView(R.id.video_step_desc_tv)
    TextView mVideoStepDescriptionTv;

    /**
     * Contains the recipe step description in the layout where the image is located at.
     */
    @BindView(R.id.image_step_desc_tv)
    TextView mImageStepDescriptionTv;

    /**
     * Displays a default image of a cook.
     */
    @BindView(R.id.cook_iv)
    ImageView mCookIv;

    /**
     * The LinearLayout changes orientation if no video or thumbnail are available.
     * The reason for this is the size of the default image which will be displayed instead.
     */
    @BindView(R.id.video_detail_layout)
    LinearLayout mVideoLinearLayout;

    /**
     * Navigates to the previous recipe step if there is one.
     */
    @BindView(R.id.prev_btn)
    Button mPreviousBtn;

    /**
     * Navigates to the next recipe step if there is one.
     */
    @BindView(R.id.next_btn)
    Button mNextBtn;

    /**
     * The Guideline which separates the navigation buttons from the step detail information.
     */
    @BindView(R.id.text_to_control_divider)
    Guideline mGuideline;

    /**
     * The ScrollView which contains the detail information view of a step if a video is
     * available.
     */
    @BindView(R.id.video_scroll_view)
    ScrollView mVideoScrollView;

    /**
     * The ScrollView which contains the detail information view of a step
     * if no video, but a image, is available.
     */
    @BindView(R.id.image_scroll_view)
    ScrollView mImageScrollView;

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
    long mVideoPosition = C.TIME_UNSET;

    /**
     * The position from where the player will resume after a state change.
     * For example: Leave app and reopen app.
     */
    long mResumePosition = C.TIME_UNSET;

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

        ButterKnife.bind(this, view);

        // Set up the Previous- and Next-Navigation-Buttons.
        if (mIsTablet || orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideNavigationButtons();

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
            Step[] steps = (Step[]) savedInstanceState.getParcelableArray(STEPS_KEY);
            if (steps != null) {
                mSteps = steps;
            }
            mPos = savedInstanceState.getInt(POS_KEY);
            mVideoPosition = savedInstanceState.getLong(VID_POS_KEY);
        }

        return view;
    }

    //region ExoPlayer resource management

    @Override
    public void onResume() {
        super.onResume();

        initializePlayer();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();

        releasePlayer();
    }

    //endregion

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
        outState.putLong(VID_POS_KEY, mResumePosition);
    }

    /**
     * Hides the navigation buttons (PREV, NEXT) from the UI without
     * having blank spaces.
     */
    private void hideNavigationButtons() {
        mPreviousBtn.setVisibility(View.GONE);
        mNextBtn.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) mGuideline.getLayoutParams();
        params.guidePercent = 1;
        mGuideline.setLayoutParams(params);
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

        mVideoStepDescriptionTv.setText(step.getDescription());

        if (!mIsTablet) {
            if (mSteps.length == 1) {
                mPreviousBtn.setVisibility(View.INVISIBLE);
                mNextBtn.setVisibility(View.INVISIBLE);
                hideNavigationButtons();
            } else if (mPos == 0) {
                mPreviousBtn.setVisibility(View.INVISIBLE);
            } else if (mPos == mSteps.length - 1) {
                mNextBtn.setVisibility(View.INVISIBLE);
            }
        }

        // If there is a video, display it!
        if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            updatePlayer(Uri.parse(step.getVideoURL()));
        } else {
            mImageStepDescriptionTv.setText(step.getDescription());

            RequestCreator requestCreator;
            // If there is a thumbnail, display it!
            if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()) {
                requestCreator = Picasso.with(getContext()).load(step.getThumbnailURL());
            } else { // If there is nothing, display the default image of a cook.
                // Hide the video-detail-view
                mVideoScrollView.setVisibility(View.GONE);
                mImageScrollView.setVisibility(View.VISIBLE);
                mCookIv.setVisibility(View.VISIBLE);

                Random random = new Random();
                int r = random.nextInt() % 2;
                int resourceId = r == 0 ? R.drawable.cook_01 : R.drawable.cook_02;
                requestCreator = Picasso.with(getContext()).load(resourceId);

                if (mIsTablet) {
                    mVideoLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
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
            if (mImageScrollView.getVisibility() == View.GONE) {
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
            } else {
                requestCreator.into(mCookIv);
            }
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

        if (mVideoPosition != C.TIME_UNSET) {
            mExoPlayer.seekTo(mVideoPosition);
        }

        mExoPlayer.prepare(mediaSource, mVideoPosition == C.TIME_UNSET, false);

        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * cleans up the ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            setResumePosition();

            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * saves the video position from where the video will resume after the state change.
     */
    private void setResumePosition() {
        mResumePosition = Math.max(0, mExoPlayer.getCurrentPosition());
    }
}
