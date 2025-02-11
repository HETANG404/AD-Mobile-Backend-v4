package com.tang.demo_db.controller;

import com.tang.demo_db.entity.Feedback;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> createFeedback(@RequestBody String feedbackText, HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            // Create feedback entity
            Feedback feedback = new Feedback();
            feedback.setUser(user);
            feedback.setContext(feedbackText);
            feedback.setTime(LocalDateTime.now());

            // Save feedback
            feedbackService.createFeedback(feedback);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error submitting feedback: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFeedback(HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            List<Feedback> feedbacks = feedbackService.getAllFeedback();
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error fetching feedback: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserFeedback(HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            List<Feedback> userFeedbacks = feedbackService.getFeedbackByUser(user);
            return ResponseEntity.ok(userFeedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error fetching user feedback: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id, HttpSession session) {
        try {
            // Check user authentication
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error deleting feedback: " + e.getMessage());
        }
    }
}