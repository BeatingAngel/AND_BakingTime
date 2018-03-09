package com.goldencrow.android.bakingtime;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.goldencrow.android.bakingtime.adapters.RecipeAdapter;
import com.goldencrow.android.bakingtime.endpoints.RecipeEndpointInterface;
import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
                        implements RecipeAdapter.RecipeOnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private ImageView mLoadingIndicatorIv;

    private RecipeAdapter mAdapter;
    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recipeListRv = findViewById(R.id.recipe_list_rv);
        mLoadingIndicatorIv = findViewById(R.id.loading_indicator_iv);

        // make the donut symbol rotate like a loading indicator
        addSpinningEffectToLoader();

        // setting up the RecyclerListView
        int gridLayoutDefaultSpanCount = 1;
        layoutManager = new GridLayoutManager(this, gridLayoutDefaultSpanCount);

        changeLayoutSpanCountFitForDevice();

        mAdapter = new RecipeAdapter(this, this);

        recipeListRv.setLayoutManager(layoutManager);
        recipeListRv.setHasFixedSize(true);
        recipeListRv.setAdapter(mAdapter);

        // getting all the data from the network and set it to the adapter.
        RecipeEndpointInterface recipeEndpoint =
                NetworkUtil.getClient().create(RecipeEndpointInterface.class);

        Call<Recipe[]> recipeCall = recipeEndpoint.doGetRecipes();
        recipeCall.enqueue(new Callback<Recipe[]>() {
            @Override
            public void onResponse(Call<Recipe[]> call, Response<Recipe[]> response) {
                removeLoadingIndicator();
                mAdapter.setRecipes(response.body());
            }

            @Override
            public void onFailure(Call<Recipe[]> call, Throwable t) {
                call.cancel();
                Log.w(TAG, t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        changeLayoutSpanCountFitForDevice();
    }

    private void changeLayoutSpanCountFitForDevice() {
        int smallSpanCount = 1;
        int mediumSpanCount = 2;
        int largeSpanCount = 3;

        int orientation = getResources().getConfiguration().orientation;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager.setSpanCount(mediumSpanCount);
            } else {
                layoutManager.setSpanCount(largeSpanCount);
            }
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager.setSpanCount(smallSpanCount);
            } else {
                layoutManager.setSpanCount(mediumSpanCount);
            }
        }
    }

    private void addSpinningEffectToLoader() {
        RotateAnimation rotate = new RotateAnimation(
                0, 180,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        mLoadingIndicatorIv.startAnimation(rotate);
    }

    private void removeLoadingIndicator() {
        mLoadingIndicatorIv.clearAnimation();
        mLoadingIndicatorIv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void OnClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.RECIPE_KEY, recipe);
        startActivity(intent);
    }
}
