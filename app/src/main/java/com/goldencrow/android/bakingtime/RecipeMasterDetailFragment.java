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
 * Created by Philipp
 */

public class RecipeMasterDetailFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private static final String STEPS_KEY = "steps";
    private static final String POS_KEY = "pos";

    SimpleExoPlayerView mExoPlayerView;
    SimpleExoPlayer mExoPlayer;

    TextView mStepDescriptionTv;
    LinearLayout mLinearLayout;

    Button mPreviousBtn;
    Button mNextBtn;

    Step[] mSteps;
    int mPos;

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

        if (savedInstanceState != null) {
            mSteps = (Step[]) savedInstanceState.getParcelableArray(STEPS_KEY);
            mPos = savedInstanceState.getInt(POS_KEY);
        }

        initializePlayer();
        updateUI();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray(STEPS_KEY, mSteps);
        outState.putInt(POS_KEY, mPos);
    }

    public void setData(Step[] steps, int pos) {
        mSteps = steps;
        mPos = pos;
    }

    public void updateUI() {
        Step step = mSteps[mPos];

        mStepDescriptionTv.setText(step.getDescription());

        if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            updatePlayer(Uri.parse(step.getVideoURL()));
        } else {
            RequestCreator requestCreator;
            if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()) {
                requestCreator = Picasso.with(getContext()).load(step.getThumbnailURL());
            } else {
                Random random = new Random();
                int r = random.nextInt() % 2;
                int resourceId = r == 0 ? R.drawable.cook_01 : R.drawable.cook_02;
                requestCreator = Picasso.with(getContext()).load(resourceId);
                mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                mExoPlayerView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                mExoPlayerView.setUseController(false);
            }

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

    private void initializePlayer() {
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        mExoPlayerView.setPlayer(mExoPlayer);
    }

    private void updatePlayer(Uri uri) {
        String userAgent = Util.getUserAgent(getContext(), "BakingTime");
        MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                getContext(), userAgent),
                new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }
}
