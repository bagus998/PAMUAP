package com.example.pamuap;

import java.util.List;

public class PlantListResponse {
    private String message;
    private List<Plant> data;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Plant> getData() { return data; }
    public void setData(List<Plant> data) { this.data = data; }
} 