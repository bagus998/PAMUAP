package com.example.pamuap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlantAdapter adapter;
    private List<Plant> plants = new ArrayList<>();
    private ApiService apiService;
    private String selectedPlantName;
    private FloatingActionButton addPlantFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.plantRecyclerView);
        addPlantFab = findViewById(R.id.addPlantFab);
        adapter = new PlantAdapter(plants, new PlantAdapter.OnPlantActionListener() {
            @Override
            public void onDetail(Plant plant) {
                Intent intent = new Intent(MainActivity.this, PlantDetailActivity.class);
                intent.putExtra("plant_name", plant.getPlantName());
                startActivity(intent);
            }
            @Override
            public void onDelete(Plant plant) {
                Log.d("PLANT_DELETE", "Trying to delete: " + plant.getPlantName());
                Toast.makeText(MainActivity.this, "Delete: " + plant.getPlantName(), Toast.LENGTH_SHORT).show();
                apiService.deletePlant(plant.getPlantName()).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Tanaman berhasil dihapus", Toast.LENGTH_SHORT).show();
                            loadPlants();
                        } else {
                            String errorMsg = "Gagal menghapus tanaman";
                            if (response.errorBody() != null) {
                                try { errorMsg = response.errorBody().string(); } catch (Exception ignored) {}
                            }
                            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        apiService = RetrofitClient.getInstance().getApiService();

        addPlantFab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPlantActivity.class);
            startActivity(intent);
        });

        loadPlants();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlants();
    }

    private void loadPlants() {
        apiService.getAllPlants().enqueue(new Callback<PlantListResponse>() {
            @Override
            public void onResponse(Call<PlantListResponse> call, Response<PlantListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    plants.clear();
                    if (response.body().getData() != null) {
                        plants.addAll(response.body().getData());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PlantListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error loading plants: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
