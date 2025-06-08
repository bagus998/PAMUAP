package com.example.pamuap;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {
    @GET("plant/all")
    Call<PlantListResponse> getAllPlants();

    @POST("plant/new")
    Call<Plant> addPlant(@Body Plant plant);

    @GET("plant/{plant_name}")
    Call<PlantResponse> getPlantByName(@Path("plant_name") String plantName);

    @PUT("plant/{plant_name}")
    Call<PlantResponse> updatePlant(@Path("plant_name") String plantName, @Body Plant plant);

    @DELETE("plant/{plant_name}")
    Call<Void> deletePlant(@Path("plant_name") String plantName);
} 