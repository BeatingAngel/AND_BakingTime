package com.goldencrow.android.bakingtime.endpoints;

import com.goldencrow.android.bakingtime.entities.Recipe;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Philipp
 */

public interface RecipeEndpointInterface {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<Recipe[]> doGetRecipes();

}
