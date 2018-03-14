package com.goldencrow.android.bakingtime.endpoints;

import com.goldencrow.android.bakingtime.entities.Recipe;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Holds the endpoints to REST-APIs.
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public interface RecipeEndpointInterface {

    /**
     * This API-site contains a json-array of all recipes.
     *
     * @return  a Call (Retrofit) which will get the JsonArray of recipes.
     */
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<Recipe[]> doGetRecipes();

}
