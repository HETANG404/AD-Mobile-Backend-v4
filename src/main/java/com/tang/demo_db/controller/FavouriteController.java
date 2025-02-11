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

@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/user")
    public ResponseEntity<List<Restaurant>> getUserFavourites(HttpSession session) {
        // Get the logged-in user from the session
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        // Fetch the user's favorite restaurants through the FavouriteService
        List<Restaurant> favouriteRestaurants = favouriteService.getFavouritesByUser(loggedInUser);
        return ResponseEntity.ok(favouriteRestaurants);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addRestaurantToFavorites(@RequestParam Long restaurantId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        try {
            favouriteService.addFavourite(loggedInUser, restaurantId);
            return ResponseEntity.ok("Restaurant added to favorites successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/isFavorite")
    public ResponseEntity<Boolean> isRestaurantFavorite(@RequestParam Long restaurantId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        boolean isFavorite = favouriteService.isRestaurantFavorite(loggedInUser, restaurantId);
        return ResponseEntity.ok(isFavorite);
    }

    @DeleteMapping("/removeFavorite")
    public ResponseEntity<String> removeFavorite(@RequestParam Long restaurantId, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            throw new RuntimeException("User not logged in");
        }

        favouriteService.removeFavourite(loggedInUser, restaurantId);
        return ResponseEntity.ok("Restaurant removed from favorites");
    }
}