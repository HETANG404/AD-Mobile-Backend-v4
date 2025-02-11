package com.tang.demo_db.controller;

import com.tang.demo_db.dto.ChangePasswordRequestDTO;
import com.tang.demo_db.dto.LoginRequest;
import com.tang.demo_db.dto.RegisterRequestDTO;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.entity.UserSession;
import com.tang.demo_db.repository.UserRepository;
import com.tang.demo_db.repository.UserSessionRepository;
import com.tang.demo_db.service.UserService;
import com.tang.demo_db.service.UserServiceZX;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/user")
public class UserControllerZX {

    @Autowired
    private UserServiceZX userService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String ipAddress = getClientIpAddress(); // 获取当前设备的IP地址

        if (session.getAttribute("user") != null) {
            return ResponseEntity.status(HttpStatus.OK).body("Already logged in");
        }

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword(), ipAddress);

        if (user != null) {
            session.setAttribute("user", user); // 记录用户 Session
            session.setAttribute("userid", user.getId());
            return ResponseEntity.status(HttpStatus.OK).body("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    // 获取当前请求的 IP 地址
    private String getClientIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }


    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            User newUser = userService.register(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getAge()
            );

            if (newUser != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
            }

        } catch (IllegalArgumentException e) {
            // 捕获密码验证错误并返回详细的错误信息
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 捕获其他未知的异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }




    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // 获取当前用户
        User user = (User) session.getAttribute("user");

        if (user != null) {
            // 获取当前设备的 IP 地址
            String ipAddress = getClientIpAddress();

            // 删除当前用户在该 IP 地址的会话记录
            List<UserSession> userSessions = userSessionRepository.findByUser(user);
            for (UserSession userSession : userSessions) {
                if (userSession.getIpAddress().equals(ipAddress)) {
                    userSessionRepository.delete(userSession); // 删除会话记录
                    break;
                }
            }

            // 清除 Session
            session.invalidate(); // 清除 session

            return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
        }

        // 如果用户没有登录（session 为 null）
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user is currently logged in");
    }



    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, HttpSession session) {
        // 检查用户是否已登录
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }

        // 原密码验证
        boolean isOldPasswordValid = userService.checkOldPassword(loggedInUser, changePasswordRequestDTO.getOldPassword());
        if (!isOldPasswordValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect old password");
        }

        // 新密码与确认密码检查
        if (!changePasswordRequestDTO.getNewPassword().equals(changePasswordRequestDTO.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New passwords do not match");
        }

        // 新密码验证
        boolean isNewPasswordValid = isValidPassword(changePasswordRequestDTO.getNewPassword());
        if (!isNewPasswordValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password does not meet the required criteria");
        }

        // 确保新密码与原密码不同
        if (changePasswordRequestDTO.getOldPassword().equals(changePasswordRequestDTO.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password cannot be the same as the old password");
        }

        // 修改密码
        boolean isPasswordChanged = userService.changePassword(loggedInUser, changePasswordRequestDTO.getNewPassword());
        if (isPasswordChanged) {
            return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change password");
        }
    }


    private boolean isValidPassword(String password) {
        // 正则表达式：至少一个大写字母、一个小写字母和一个数字
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,}$";
        return Pattern.matches(passwordRegex, password);
    }


    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        // 1. 从请求体中获取用户的 email
        String email = request.get("email");

        // 2. 确保用户提供了 email
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }

        // 3. 从数据库中找到用户
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with the provided email");
        }

        // 4. 调用服务层方法重置密码
        boolean isReset = userService.resetPassword(user);
        if (isReset) {
            return ResponseEntity.ok("Password reset successfully. Check your email for the new password.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password");
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        // Get the current user from session
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Create response with required user info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("age", user.getAge());

        return ResponseEntity.ok(userInfo);
    }

}