package com.goldencrow.android.bakingtime;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.goldencrow.android.bakingtime.endpoints.RecipeEndpointInterface;
import com.goldencrow.android.bakingtime.entities.Recipe;
import com.goldencrow.android.bakingtime.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private RecyclerView mRecipeListRv;

    private RecipeAdapter mAdapter;
    private GridLayoutManager layoutManager;

    private RecipeEndpointInterface mRecipeEndpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipeListRv = findViewById(R.id.recipe_list_rv);

        int gridLayoutDefaultSpanCount = 1;
        layoutManager = new GridLayoutManager(this, gridLayoutDefaultSpanCount);

        changeLayoutSpanCountFitForDevice();

        mAdapter = new RecipeAdapter(this);

        mRecipeListRv.setLayoutManager(layoutManager);
        mRecipeListRv.setHasFixedSize(true);
        mRecipeListRv.setAdapter(mAdapter);

        mRecipeEndpoint = NetworkUtil.getClient().create(RecipeEndpointInterface.class);

        Call<Recipe[]> recipeCall = mRecipeEndpoint.doGetRecipes();
        recipeCall.enqueue(new Callback<Recipe[]>() {
            @Override
            public void onResponse(Call<Recipe[]> call, Response<Recipe[]> response) {
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
}
