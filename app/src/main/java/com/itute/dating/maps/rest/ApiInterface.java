package com.itute.dating.maps.rest;

import com.itute.dating.maps.model.DirectionResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by buivu on 17/11/2016.
 */
public interface ApiInterface {
    @GET("/maps/api/directions/json")
    Call<DirectionResults> getJson(@Query("origin") String origin, @Query("destination") String destination);
}
