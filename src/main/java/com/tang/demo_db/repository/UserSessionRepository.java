package com.tang.demo_db.repository;

import com.tang.demo_db.entity.User;
import com.tang.demo_db.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUser(User user);
}
