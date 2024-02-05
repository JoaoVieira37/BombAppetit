package com.vieira.joao.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Review implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;
    @ManyToOne
    private AppUser appUser;
    @ManyToOne
    private RestaurantInfo restaurantInfo;
    private Integer rating;
    private String review;

    public Review() {
    }

    public Review(Integer id, AppUser appUser, RestaurantInfo restaurantInfo, Integer rating, String review) {
        this.id = id;
        this.appUser = appUser;
        this.restaurantInfo = restaurantInfo;
        this.rating = rating;
        this.review = review;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "{" +
                "\"user\" : \"" + appUser.getUsername() + "\"" +
                ", \"restaurant\" : \"" + restaurantInfo.getRestaurant() + "\"" +
                ", \"rating\": " + rating +
                ", \"review\":\"" + review + "\"" +
                "}";
    }
}
