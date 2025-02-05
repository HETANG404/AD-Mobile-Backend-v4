package com.tang.demo_db.controller;

import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.repository.UserRepository;
import com.tang.demo_db.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/favorites")
public class FavouriteController {

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private UserRepository userRepository;

    // 获取用户收藏的餐厅
    @GetMapping
    public ResponseEntity<List<Favourite>> getFavorites(@RequestParam("userId") Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(favouriteService.getUserFavourites(userOptional.get()));
    }

    // 添加餐厅到收藏
    @PostMapping("/add")
    public ResponseEntity<Void> addFavorite(@RequestParam("userId") Long userId, @RequestBody Favourite favourite) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
       favourite.setUser(userOptional.get());
        favouriteService.addFavourite(favourite);
        return ResponseEntity.ok().build();
    }

    // 从收藏中移除餐厅
    @DeleteMapping("/remove/{restaurantId}")
    public ResponseEntity<Void> removeFavorite(@RequestParam("userId") Long userId, @PathVariable Long restaurantId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        favouriteService.removeFavourite(userOptional.get(), restaurantId);
        return ResponseEntity.ok().build();
    }
}
