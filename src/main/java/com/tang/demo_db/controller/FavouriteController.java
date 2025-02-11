package com.tang.demo_db.controller;

import com.tang.demo_db.dto.FavoriteRequest;
import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.repository.RestaurantRepository;
import com.tang.demo_db.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favourites")
public class FavouriteController {

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @PostMapping
    public ResponseEntity<?> createFavourite(@RequestBody FavoriteRequest request, HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            // Find or create restaurant
            Restaurant restaurant = restaurantRepository.findByPlaceId(request.getPlaceId())
                    .orElseGet(() -> {
                        Restaurant newRestaurant = new Restaurant();
                        newRestaurant.setPlaceId(request.getPlaceId());
                        return restaurantRepository.save(newRestaurant);
                    });

            // Create favourite
            Favourite favourite = new Favourite();
            favourite.setUser(user);
            favourite.setRestaurant(restaurant);
            favouriteService.createFavourite(favourite);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error creating favourite: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFavourites(HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            // Get favorites and extract place IDs
            List<String> placeIds = favouriteService.getAllFavourites().stream()
                    .map(favourite -> favourite.getRestaurant().getPlaceId())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(placeIds);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error fetching favourites: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavourite(@PathVariable Long id, HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            favouriteService.deleteFavourite(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error deleting favourite: " + e.getMessage());
        }
    }

    @DeleteMapping("/byPlaceId/{placeId}")
    public ResponseEntity<?> deleteFavouriteByPlaceId(@PathVariable String placeId, HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            // Find restaurant by placeId
            Restaurant restaurant = restaurantRepository.findByPlaceId(placeId)
                    .orElse(null);

            if (restaurant == null) {
                return ResponseEntity.notFound().build();
            }

            // Delete favorite for this user and restaurant
            favouriteService.deleteFavouriteByUserAndRestaurant(user, restaurant);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error deleting favourite: " + e.getMessage());
        }
    }
}