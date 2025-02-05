package com.tang.demo_db.service;

import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.repository.FavouriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavouriteService {

    @Autowired
    private FavouriteRepository favouriteRepository;

    public List<Favourite> getUserFavourites(User user) {
        return favouriteRepository.findByUser(user);
    }

    public void addFavourite(Favourite favourite) {
        favouriteRepository.save(favourite);
    }

    public void removeFavourite(User user, Long restaurantId) {
        favouriteRepository.deleteByUserAndRestaurantId(user, restaurantId);
    }
}
