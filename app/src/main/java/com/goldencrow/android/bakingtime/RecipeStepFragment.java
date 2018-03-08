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
import com.squareup.picasso.Target;

/**
 * Created by Philipp
 */

public class RecipeStepFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    SimpleExoPlayerView mExoPlayerView;
    SimpleExoPlayer mExoPlayer;

    TextView mStepDescriptionTv;

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
        mPreviousBtn = view.findViewById(R.id.prev_btn);
        mNextBtn = view.findViewById(R.id.next_btn);

        mPreviousBtn.setVisibility(View.INVISIBLE);
        mNextBtn.setVisibility(View.INVISIBLE);

        initializePlayer();
        updateUI();

        return view;
    }

    public void setData(Step[] steps, int pos) {
        mSteps = steps;
        mPos = pos;
    }

    public void updateUI() {
        Step step = mSteps[mPos];

        mStepDescriptionTv.setText(step.getDescription());

        if (!step.getVideoURL().isEmpty()) {
            updatePlayer(Uri.parse(step.getVideoURL()));
        }
        if (!step.getThumbnailURL().isEmpty()) {
            Picasso.with(getContext())
                    .load(step.getThumbnailURL())
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
