package sirs.a14.bombappetit.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
public class RestaurantInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;
    private String owner;
    private String restaurant;
    private String address;
    private List<String> genre;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MenuEntry> menu;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Review> reviews;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MealVoucher> mealVoucher;

    public RestaurantInfo() {
    }

    public RestaurantInfo(String restaurant, String address, String owner, List<String> genre) {
        this.restaurant = restaurant;
        this.address = address;
        this.owner = owner;
        this.genre = genre;
    }

    public RestaurantInfo(String restaurant, String address, String owner, List<String> genre, List<MenuEntry> menu, List<MealVoucher> vouchers) {
        this.restaurant = restaurant;
        this.address = address;
        this.owner = owner;
        this.genre = genre;
        this.menu = menu;
        this.mealVoucher = vouchers;
    }

    public RestaurantInfo(Integer id, String owner, String restaurant, String address, List<String> genre, List<MenuEntry> menu, List<Review> reviews, List<MealVoucher> mealVoucher) {
        this.id = id;
        this.owner = owner;
        this.restaurant = restaurant;
        this.address = address;
        this.genre = genre;
        this.menu = menu;
        this.reviews = reviews;
        this.mealVoucher = mealVoucher;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<MealVoucher> getMealVoucher() {
        return mealVoucher;
    }

    public void setMealVoucher(List<MealVoucher> vouchers) {
        this.mealVoucher = vouchers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<MenuEntry> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuEntry> menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "{" +
        " \"owner\" : \"" + owner + "\"" +
        ", \"restaurant\" : \"" + restaurant + "\"" +
        ", \"address\" : \"" + address + "\"" +
        ", \"genre\" :" + genre +
        ", \"menu\" :" + menu +
        //", reviews=" + reviews +
        //", mealVoucher=" + mealVoucher +
        "}";
    }
}
