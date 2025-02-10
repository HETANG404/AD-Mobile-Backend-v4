package com.tang.demo_db.service;

import com.tang.demo_db.dao.UserDAO;
import com.tang.demo_db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public List<User> getAllUsers() {
        return userDAO.findAllUsers();
    }

    public User getUserById(Long id) {
        return userDAO.findUserById(id);
    }

    public void createUser(User user) {
        userDAO.saveUser(user);
    }

    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    public void deleteUser(Long id) {
        userDAO.deleteUser(id);
    }
}
