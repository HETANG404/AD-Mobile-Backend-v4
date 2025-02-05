package com.tang.demo_db;

import com.tang.demo_db.controller.FavouriteController;
import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.repository.UserRepository;
import com.tang.demo_db.service.FavouriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    private UserRepository userRepository;  // ✅ 添加 Mock UserRepository

    @InjectMocks
    private FavouriteController favouriteController;

    @Test
    void testGetFavorites() {
        // 1. 准备模拟数据
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        Favourite fav1 = new Favourite(1L, mockUser, null);
        Favourite fav2 = new Favourite(2L, mockUser, null);
        List<Favourite> mockFavorites = Arrays.asList(fav1, fav2);

        // 2. 模拟 userRepository.findById() 返回 Optional.of(mockUser)
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(favouriteService.getUserFavourites(mockUser)).thenReturn(mockFavorites);

        // 3. 调用 Controller 方法
        ResponseEntity<List<Favourite>> response = favouriteController.getFavorites(userId);

        // 4. 断言测试结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());

        // 5. 验证 service 方法是否被调用
        verify(userRepository, times(1)).findById(userId);
        verify(favouriteService, times(1)).getUserFavourites(mockUser);
    }
}
