package com.tang.demo_db.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "query_log")
public class QueryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Getters and Setters
}
