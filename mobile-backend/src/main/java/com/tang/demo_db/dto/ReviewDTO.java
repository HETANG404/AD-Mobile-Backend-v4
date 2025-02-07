package com.tang.demo_db.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private String authorName;
    private Double rating;
    private String text;
    private String time;
    private String relativeTimeDescription;
}