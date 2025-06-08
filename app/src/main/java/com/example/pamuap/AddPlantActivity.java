package com.example.pamuap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlantActivity extends AppCompatActivity {
    private EditText plantNameEditText, plantPriceEditText, plantDescriptionEditText;
    private Button saveButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        plantNameEditText = findViewById(R.id.plantNameEditText);
        plantPriceEditText = findViewById(R.id.plantPriceText);
        plantDescriptionEditText = findViewById(R.id.plantDescriptionText);
        saveButton = findViewById(R.id.saveButton);
        apiService = RetrofitClient.getInstance().getApiService();

        saveButton.setOnClickListener(v -> addPlant());
    }

    private void addPlant() {
        String plantName = plantNameEditText.getText().toString().trim();
        String price = plantPriceEditText.getText().toString().trim();
        String description = plantDescriptionEditText.getText().toString().trim();

        if (plantName.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        Plant plant = new Plant(plantName, description, price);
        apiService.addPlant(plant).enqueue(new Callback<Plant>() {
            @Override
            public void onResponse(Call<Plant> call, Response<Plant> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddPlantActivity.this, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddPlantActivity.this, "Gagal menambah tanaman", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Plant> call, Throwable t) {
                Toast.makeText(AddPlantActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 