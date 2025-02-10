package com.tang.demo_db;

import com.tang.demo_db.controller.FavouriteController;
import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.repository.RestaurantRepository;
import com.tang.demo_db.repository.UserRepository;
import com.tang.demo_db.service.FavouriteService;
import com.tang.demo_db.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoDbApplicationTests {

    @Mock
    private FavouriteService favouriteService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;  // 需要 Mock 餐厅数据

    @Mock
    private UserService userService;

    @InjectMocks
    private FavouriteController favouriteController;

    private User mockUser;
    private Restaurant mockRestaurant1;
    private Restaurant mockRestaurant2;
    private List<Favourite> mockFavorites;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建模拟用户
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Alice");

        // 创建模拟餐厅对象
        mockRestaurant1 = new Restaurant();
        mockRestaurant1.setId(101L);
        mockRestaurant1.setName("Sushi House");

        mockRestaurant2 = new Restaurant();
        mockRestaurant2.setId(202L);
        mockRestaurant2.setName("Pizza Place");

        // 初始化收藏列表
        Favourite fav1 = new Favourite();
        fav1.setId(1L);
        fav1.setUser(mockUser);
        fav1.setRestaurant(mockRestaurant1);

        Favourite fav2 = new Favourite();
        fav2.setId(2L);
        fav2.setUser(mockUser);
        fav2.setRestaurant(mockRestaurant2);

        mockFavorites = Arrays.asList(fav1, fav2);
    }

    // 测试获取用户收藏列表（成功）
    @Test
    void testGetFavorites() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(favouriteService.getUserFavourites(mockUser)).thenReturn(mockFavorites);

        ResponseEntity<List<Favourite>> response = favouriteController.getFavorites(mockUser.getId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(favouriteService, times(1)).getUserFavourites(mockUser);
    }

    // 测试获取用户收藏列表（用户不存在）
    @Test
    void testGetFavorites_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<List<Favourite>> response = favouriteController.getFavorites(2L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(userRepository, times(1)).findById(2L);
        verify(favouriteService, times(0)).getUserFavourites(any());
    }

    // 测试添加收藏（成功）
    @Test
    void testAddFavorite() {
        Long restaurantId = 303L;
        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setId(restaurantId);
        newRestaurant.setName("Burger King");

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(newRestaurant));
        doNothing().when(favouriteService).addFavourite(mockUser, restaurantId);

        ResponseEntity<Void> response = favouriteController.addFavorite(mockUser.getId(), restaurantId);

        assertEquals(200, response.getStatusCodeValue());

        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(favouriteService, times(1)).addFavourite(mockUser, restaurantId);
    }

    // 测试添加收藏（用户不存在）
    @Test
    void testAddFavorite_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = favouriteController.addFavorite(2L, 303L);

        assertEquals(404, response.getStatusCodeValue());

        verify(userRepository, times(1)).findById(2L);
        verify(restaurantRepository, times(0)).findById(anyLong());
        verify(favouriteService, times(0)).addFavourite(any(), anyLong());
    }

    // 测试添加收藏（餐厅不存在）
    @Test
    void testAddFavorite_RestaurantNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(restaurantRepository.findById(303L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = favouriteController.addFavorite(mockUser.getId(), 303L);

        assertEquals(404, response.getStatusCodeValue());

        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(restaurantRepository, times(1)).findById(303L);
        verify(favouriteService, times(0)).addFavourite(any(), anyLong());
    }

    // 测试移除收藏（成功）
    @Test
    void testRemoveFavorite() {
        Long restaurantId = 101L;

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        doNothing().when(favouriteService).removeFavourite(mockUser, restaurantId);

        ResponseEntity<Void> response = favouriteController.removeFavorite(mockUser.getId(), restaurantId);

        assertEquals(200, response.getStatusCodeValue());

        verify(userRepository, times(1)).findById(mockUser.getId());
        verify(favouriteService, times(1)).removeFavourite(mockUser, restaurantId);
    }

    // 测试移除收藏（用户不存在）
    @Test
    void testRemoveFavorite_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = favouriteController.removeFavorite(2L, 101L);

        assertEquals(404, response.getStatusCodeValue());

        verify(userRepository, times(1)).findById(2L);
        verify(favouriteService, times(0)).removeFavourite(any(), anyLong());
    }
}
