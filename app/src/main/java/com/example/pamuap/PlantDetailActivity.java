package com.example.pamuap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantDetailActivity extends AppCompatActivity {
    private static final String TAG = "PlantDetailActivity";
    private TextView plantName, plantType, plantStatus;
    private Button editButton, deleteButton;
    private ApiService apiService;
    private String plantNameValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        plantName = findViewById(R.id.plantName);
        plantType = findViewById(R.id.plantType);
        plantStatus = findViewById(R.id.plantStatus);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        apiService = RetrofitClient.getInstance().getApiService();

        // Get plant name from intent
        plantNameValue = getIntent().getStringExtra("plant_name");
        if (plantNameValue != null) {
            getSupportActionBar().setTitle(plantNameValue);
            loadPlant(plantNameValue);
        } else {
            Log.e(TAG, "No plant name provided in intent");
            Toast.makeText(this, "Error: No plant selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup button click listeners
        editButton.setOnClickListener(v -> {
            Log.d(TAG, "Edit button clicked for plant: " + plantNameValue);
            Intent intent = new Intent(PlantDetailActivity.this, EditPlantActivity.class);
            intent.putExtra("plant_name", plantNameValue);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "Delete button clicked for plant: " + plantNameValue);
            deletePlant();
        });
    }

    private void loadPlant(String plantNameStr) {
        Log.d(TAG, "Loading plant details for: " + plantNameStr);
        apiService.getPlantByName(plantNameStr).enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Plant plant = response.body().getData();
                    plantName.setText(plant.getPlantName());
                    plantType.setText(plant.getPrice());
                    plantStatus.setText(plant.getDescription());
                    Log.d(TAG, "Successfully loaded plant details");
                } else {
                    String errorMsg = "Tanaman tidak ditemukan";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "Error loading plant: " + errorMsg);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(PlantDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Log.e(TAG, "Network error loading plant", t);
                Toast.makeText(PlantDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void deletePlant() {
        Log.d(TAG, "Attempting to delete plant: " + plantNameValue);
        apiService.deletePlant(plantNameValue).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successfully deleted plant");
                    Toast.makeText(PlantDetailActivity.this, "Tanaman berhasil dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Gagal menghapus tanaman";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "Error deleting plant: " + errorMsg);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(PlantDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error deleting plant", t);
                Toast.makeText(PlantDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 