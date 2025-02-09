package com.tang.demo_db.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ExternalApiService {

    @Value("${food.recommender.api.url}")
    private String foodRecommenderApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ExternalApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public ResponseEntity<String> getRecommendations(String userQuery) {
        String encodedQuery = UriComponentsBuilder
                .fromHttpUrl(foodRecommenderApiUrl)
                .queryParam("query", userQuery)
                .build()
                .toUriString();

        return restTemplate.getForEntity(encodedQuery, String.class);
    }
}