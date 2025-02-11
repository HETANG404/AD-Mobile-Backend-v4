package com.tang.demo_db.repository;

import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findByUser(User user);
    void deleteByUserAndRestaurant(User user, Restaurant restaurant);
}