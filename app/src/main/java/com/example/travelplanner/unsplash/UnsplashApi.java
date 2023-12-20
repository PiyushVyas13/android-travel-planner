package com.example.travelplanner.unsplash;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface UnsplashApi {
    @Headers({"Authorization: Client-ID K0eIbhXfonc3UuKiLVp6s4QTx1Q1glzQC0DA9s-lAh4"})
    @GET("/search/photos")
    Call<UnsplashResponse> getPhotos(@Query("query") String query);

}
