package com.example.pamuap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
    private List<Plant> plants;
    private OnPlantActionListener listener;

    public interface OnPlantActionListener {
        void onDetail(Plant plant);
        void onDelete(Plant plant);
    }

    public PlantAdapter(List<Plant> plants, OnPlantActionListener listener) {
        this.plants = plants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.nameText.setText(plant.getPlantName());
        holder.priceText.setText(plant.getPrice());
        holder.descriptionText.setText(plant.getDescription());
        holder.detailButton.setOnClickListener(v -> listener.onDetail(plant));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(plant));
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, priceText, descriptionText;
        Button detailButton, deleteButton;
        ImageView plantImage;

        PlantViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.plantName);
            priceText = itemView.findViewById(R.id.plantType);
            descriptionText = itemView.findViewById(R.id.plantStatus);
            detailButton = itemView.findViewById(R.id.detailButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            plantImage = itemView.findViewById(R.id.plantImage);
        }
    }
} 