package com.vieira.joao.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class MealVoucher implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;
    private String code;
    private String description;
    @ManyToOne
    private RestaurantInfo restaurantInfo;
    @ManyToOne
    private AppUser appUser;

    public MealVoucher() {
    }

    public MealVoucher(String code, String description, RestaurantInfo restaurantInfo, AppUser appUser) {
        this.code = code;
        this.description = description;
        this.restaurantInfo = restaurantInfo;
        this.appUser = appUser;
    }

    public MealVoucher(Integer id, String code, String description, RestaurantInfo restaurantInfo) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.restaurantInfo = restaurantInfo;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
    }

    @Override
    public String toString() {
        return "{" +
                " \"id\" : " + id +
                ", \"code\" :\"" + code + "\"" +
                ", \"description\": \"" + description + "\"" +
                "}";
    }

}
