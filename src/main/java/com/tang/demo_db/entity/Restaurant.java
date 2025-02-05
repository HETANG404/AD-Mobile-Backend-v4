package com.tang.demo_db.entity;


import jakarta.persistence.*;


@Entity
@Table(name = "restaurant")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private Double rating;
    private Integer userRatingsTotal;

    @Column(nullable = false)
    private String types;

    private Integer priceLevel;
    private String phoneNumber;
    private String website;
    private String photoReference;

    private Double latitude;
    private Double longitude;

    // Getters and Setters
}
