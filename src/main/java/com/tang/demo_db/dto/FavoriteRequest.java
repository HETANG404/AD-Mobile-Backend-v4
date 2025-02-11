package com.tang.demo_db.dto;

public class FavoriteRequest {
    private String placeId;

    // Constructor
    public FavoriteRequest() {
    }

    public FavoriteRequest(String placeId) {
        this.placeId = placeId;
    }

    // Getters and setters
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}