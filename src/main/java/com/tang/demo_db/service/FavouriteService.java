package com.tang.demo_db.service;

import com.tang.demo_db.dao.FavouriteDAO;
import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.repository.FavouriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FavouriteService {

    @Autowired
    private FavouriteDAO favouriteDAO;

    @Autowired
    private FavouriteRepository favouriteRepository;

    public List<Favourite> getAllFavourites() {
        return favouriteDAO.findAllFavourites();
    }

    public Favourite getFavouriteById(Long id) {
        return favouriteDAO.findFavouriteById(id);
    }

    public void createFavourite(Favourite favourite) {
        favouriteDAO.saveFavourite(favourite);
    }

    public void updateFavourite(Favourite favourite) {
        favouriteDAO.updateFavourite(favourite);
    }

    public void deleteFavourite(Long id) {
        favouriteDAO.deleteFavourite(id);
    }

    public void deleteFavouriteByUserAndRestaurant(User user, Restaurant restaurant) {
        favouriteRepository.deleteByUserAndRestaurant(user, restaurant);
    }
}