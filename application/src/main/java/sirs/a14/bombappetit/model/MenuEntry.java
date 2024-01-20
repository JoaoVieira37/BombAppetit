package sirs.a14.bombappetit.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class MenuEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;
    private String itemName;
    private String category;
    private String description;
    private Float price;
    private String currency;
    @ManyToOne
    private RestaurantInfo restaurantInfo;

    public MenuEntry() {
    }

    public MenuEntry(String itemName, String category, String description, Float price, String currency) {
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.currency = currency;
    }

    public MenuEntry(String itemName, String category, String description, Float price, String currency, RestaurantInfo restaurantInfo) {
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.restaurantInfo = restaurantInfo;
    }

    public MenuEntry(Integer id, String itemName, String category, String description, Float price, String currency, RestaurantInfo restaurantInfo) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.restaurantInfo = restaurantInfo;
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "{" +
        " \"itemName\" : \"" + itemName + "\""+
        ", \"category\" : \"" + category + "\"" +
        ", \"description\" : \"" + description + "\"" +
        ", \"price\" : " + price +
        ", \"currency\" : \"" + currency + "\"" +
        "}";
    }
}