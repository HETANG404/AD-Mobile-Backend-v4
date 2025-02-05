package com.tang.demo_db.repository;

import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findByUser(User user);
    void deleteByUserAndRestaurantId(User user, Long restaurantId);
}
