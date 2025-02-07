package com.tang.demo_db.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.demo_db.dto.RecommendationResponseDTO;
import com.tang.demo_db.dto.RestaurantDTO;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public ResponseEntity<?> processApiResponse(String apiResponse) {
        try {
            // Parse the API response
            RecommendationResponseDTO response = objectMapper.readValue(apiResponse, RecommendationResponseDTO.class);

            // If it's not a success response or no restaurants, return the response as is
            if (!response.isSuccess() || response.getRestaurants() == null || response.getRestaurants().isEmpty()) {
                return ResponseEntity.ok(response);
            }

            // Process and store restaurants
            List<Restaurant> savedRestaurants = response.getRestaurants().stream()
                    .map(this::saveOrUpdateRestaurant)
                    .collect(Collectors.toList());

            log.info("Processed {} restaurants", savedRestaurants.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing restaurant data", e);
            return ResponseEntity.badRequest()
                    .body("Error processing restaurant data: " + e.getMessage());
        }
    }

    private Restaurant saveOrUpdateRestaurant(RestaurantDTO dto) {
        Optional<Restaurant> existingRestaurant = restaurantRepository.findByPlaceId(dto.getPlaceId());

        Restaurant restaurant = existingRestaurant.orElse(new Restaurant());

        // Update basic fields
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());
        restaurant.setRating(dto.getRating());
        restaurant.setUserRatingsTotal(dto.getUserRatingsTotal());
        restaurant.setPlaceId(dto.getPlaceId());
        restaurant.setPriceLevel(dto.getPriceLevel());
        restaurant.setPhone(dto.getPhone());  // Changed from setPhoneNumber to setPhone to match entity
        restaurant.setWebsite(dto.getWebsite());

        // Handle location
        if (dto.getLocation() != null) {
            restaurant.setLatitude(dto.getLocation().getLat());
            restaurant.setLongitude(dto.getLocation().getLng());
        }

        // Handle types
        if (dto.getTypes() != null) {
            restaurant.setTypes(String.join(",", dto.getTypes()));
        }

        // Handle photos - convert to JSON string
        if (dto.getPhotos() != null) {
            try {
                restaurant.setPhotos(objectMapper.writeValueAsString(dto.getPhotos()));
            } catch (Exception e) {
                log.error("Error converting photos to JSON", e);
            }
        }

        // Set scores
        restaurant.setPredictedScore(dto.getPredictedScore());
        restaurant.setAdjustedScore(dto.getAdjustedScore());

        // Handle opening hours - convert to JSON string
        if (dto.getOpeningHours() != null) {
            try {
                restaurant.setOpeningHours(objectMapper.writeValueAsString(dto.getOpeningHours()));
            } catch (Exception e) {
                log.error("Error converting opening hours to JSON", e);
            }
        }

        return restaurantRepository.save(restaurant);
    }
}