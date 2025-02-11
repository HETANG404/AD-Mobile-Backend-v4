package com.tang.demo_db.service;

import com.tang.demo_db.dao.FavouriteDAO;
import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.repository.FavouriteRepository;
import com.tang.demo_db.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavouriteService {

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Restaurant> getFavouritesByUser(User user) {
        List<Favourite> favourites = favouriteRepository.findByUser(user);
        return favourites.stream()
                .map(Favourite::getRestaurant)
                .collect(Collectors.toList());
    }

    public void addFavourite(User user, Long restaurantId) {
        // Check if already exists
        Favourite existingFavourite = favouriteRepository.findByUserAndRestaurantId(user, restaurantId);
        if (existingFavourite != null) {
            throw new RuntimeException("Restaurant already in favorites");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setRestaurant(restaurant);
        favouriteRepository.save(favourite);
    }

    public void removeFavourite(User user, Long restaurantId) {
        Favourite favourite = favouriteRepository.findByUserAndRestaurantId(user, restaurantId);
        if (favourite != null) {
            favouriteRepository.delete(favourite);
        }
    }

    public boolean isRestaurantFavorite(User user, Long restaurantId) {
        return favouriteRepository.findByUserAndRestaurantId(user, restaurantId) != null;
    }

    // Helper method to get all favorite restaurant IDs for a user
    public Set<Long> getFavoriteRestaurantIds(User user) {
        return favouriteRepository.findByUser(user).stream()
                .map(favourite -> favourite.getRestaurant().getId())
                .collect(Collectors.toSet());
    }
}