package com.example.pamuap;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPlantActivity extends AppCompatActivity {
    private static final String TAG = "EditPlantActivity";
    private EditText plantNameEditText, plantPriceEditText, plantDescriptionEditText;
    private Button updateButton;
    private ApiService apiService;
    private String originalPlantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Tanaman");

        // Initialize views
        plantNameEditText = findViewById(R.id.plantNameEditText);
        plantPriceEditText = findViewById(R.id.plantPriceEditText);
        plantDescriptionEditText = findViewById(R.id.plantDescriptionEditText);
        updateButton = findViewById(R.id.updateButton);
        apiService = RetrofitClient.getInstance().getApiService();

        // Get plant name from intent
        originalPlantName = getIntent().getStringExtra("plant_name");
        if (originalPlantName != null) {
            loadPlant(originalPlantName);
        } else {
            Log.e(TAG, "No plant name provided in intent");
            Toast.makeText(this, "Error: No plant selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateButton.setOnClickListener(v -> updatePlant());
    }

    private void loadPlant(String plantName) {
        Log.d(TAG, "Loading plant details for: " + plantName);
        apiService.getPlantByName(plantName).enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Plant plant = response.body().getData();
                    plantNameEditText.setText(plant.getPlantName());
                    plantPriceEditText.setText(plant.getPrice());
                    plantDescriptionEditText.setText(plant.getDescription());
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
                    Toast.makeText(EditPlantActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Log.e(TAG, "Network error loading plant", t);
                Toast.makeText(EditPlantActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updatePlant() {
        String plantName = plantNameEditText.getText().toString().trim();
        String price = plantPriceEditText.getText().toString().trim();
        String description = plantDescriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(plantName) || TextUtils.isEmpty(price) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Attempting to update plant: " + originalPlantName);
        Plant plant = new Plant(plantName, description, price);
        apiService.updatePlant(originalPlantName, plant).enqueue(new Callback<PlantResponse>() {
            @Override
            public void onResponse(Call<PlantResponse> call, Response<PlantResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Log.d(TAG, "Successfully updated plant");
                    Toast.makeText(EditPlantActivity.this, "Tanaman berhasil diupdate", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Gagal update tanaman";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "Error updating plant: " + errorMsg);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(EditPlantActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlantResponse> call, Throwable t) {
                Log.e(TAG, "Network error updating plant", t);
                Toast.makeText(EditPlantActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 