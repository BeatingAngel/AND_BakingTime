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

import static com.goldencrow.android.bakingtime.RecipeDetailActivity.RECIPE_STEPS_KEY;
import static com.goldencrow.android.bakingtime.RecipeDetailActivity.RECIPE_STEP_POS_KEY;

/**
 *
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class RecipeStepActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    SimpleExoPlayerView mExoPlayerView;
    SimpleExoPlayer mExoPlayer;

    LinearLayout mLinearLayout;

    TextView mStepDescriptionTv;

    Button mPreviousBtn;
    Button mNextBtn;

    ArrayList<Step> stepsList;
    int pos;

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
            stepsList = intent.getParcelableArrayListExtra(RECIPE_STEPS_KEY);
            pos = intent.getIntExtra(RECIPE_STEP_POS_KEY, 0);

            initializePlayer();
            updateUI();

            mPreviousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pos > 0) {
                        pos--;
                        launchNewStepIntent();
                    }
                }
            });
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pos < stepsList.size() - 1) {
                        pos++;
                        launchNewStepIntent();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releasePlayer();
    }

    private void launchNewStepIntent() {
        mExoPlayer.stop();
        Intent nextStepIntent = new Intent(
                RecipeStepActivity.this, RecipeStepActivity.class);
        nextStepIntent.putExtra(RECIPE_STEP_POS_KEY, pos);
        nextStepIntent.putParcelableArrayListExtra(RECIPE_STEPS_KEY, stepsList);
        finish();
        startActivity(nextStepIntent);
    }

    private void updateUI() {
        int ingredientsPosition = 0;
        Step step = stepsList.get(pos);

        mStepDescriptionTv.setText(step.getDescription());
        if (pos == ingredientsPosition) {
            mPreviousBtn.setVisibility(View.INVISIBLE);
        }
        if (pos == stepsList.size() - 1) {
            mNextBtn.setVisibility(View.INVISIBLE);
        }

        if (step.getVideoURL() != null && !step.getVideoURL().isEmpty()) {
            updatePlayer(Uri.parse(step.getVideoURL()));
        } else {
            RequestCreator requestCreator;
            if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()) {
                requestCreator = Picasso.with(this).load(step.getThumbnailURL());
            } else {
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
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        mExoPlayerView.setPlayer(mExoPlayer);
    }

    private void updatePlayer(Uri uri) {
        String userAgent = Util.getUserAgent(this, "BakingTime");
        MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                this, userAgent), new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }
}
