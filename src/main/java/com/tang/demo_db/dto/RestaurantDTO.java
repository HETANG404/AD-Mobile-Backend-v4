package com.tang.demo_db.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RestaurantDTO {
    private String name;
    private String address;
    private Double rating;
    private Integer userRatingsTotal;
    private Integer priceLevel;
    private String placeId;
    private LocationDTO location;
    private List<String> types;
    private String website;
    private String phone;
    private List<String> photos;
    private Double predictedScore;
    private Double adjustedScore;
    private List<ReviewDTO> reviews;
    private Map<String, Object> openingHours;
    private LocationContextDTO locationContext;
}