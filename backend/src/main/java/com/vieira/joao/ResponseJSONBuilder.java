package com.vieira.joao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vieira.joao.model.*;

import java.util.List;

import static java.lang.String.valueOf;

public class ResponseJSONBuilder {

    public static String buildInfoResponse(List<RestaurantInfo> restaurantInfoList) {

        JsonObject baseJsonObject = new JsonObject();
        JsonArray restaurantInfoArray = new JsonArray();

        for (RestaurantInfo restaurantInfo : restaurantInfoList) {
            JsonObject restaurantInfoEntry = new JsonObject();
            restaurantInfoEntry.addProperty("id", restaurantInfo.getId());
            restaurantInfoEntry.addProperty("name", restaurantInfo.getRestaurant());
            restaurantInfoArray.add(restaurantInfoEntry);
        }

        baseJsonObject.add("restaurants", restaurantInfoArray);

        return new Gson().toJson(baseJsonObject);
    }

    public static String buildUsersResponse(List<AppUser> appUserList) {

        JsonObject baseJsonObject = new JsonObject();
        JsonArray usersArray = new JsonArray();

        for (AppUser appUser : appUserList) {
            JsonObject userEntry = new JsonObject();
            userEntry.addProperty("id", appUser.getId());
            userEntry.addProperty("name", appUser.getUsername());
            usersArray.add(userEntry);
        }

        baseJsonObject.add("users", usersArray);
        return new Gson().toJson(baseJsonObject);
    }

    public static String buildRestaurantInfoResponse(RestaurantInfo restaurantInfo, String user) {

        JsonObject baseJsonObject = new JsonObject();
        JsonObject restaurantInfoObject = new JsonObject();

        restaurantInfoObject.addProperty("owner", restaurantInfo.getOwner());
        restaurantInfoObject.addProperty("restaurant", restaurantInfo.getRestaurant());
        restaurantInfoObject.addProperty("address", restaurantInfo.getAddress());

        JsonArray genreArray = new JsonArray();
        for (String genre : restaurantInfo.getGenre()) { genreArray.add(genre); }
        restaurantInfoObject.add("genre", genreArray);
        restaurantInfoObject.add("menu",
                JsonParser.parseString(
                        ResponseJSONBuilder.buildMenuResponse(restaurantInfo.getMenu())));

        if (!restaurantInfo.getReviews().isEmpty()) {
            restaurantInfoObject.add("reviews",
                    JsonParser.parseString(
                            ResponseJSONBuilder.buildReviewsResponse(restaurantInfo.getReviews())));

        }

        if (!restaurantInfo.getMealVoucher().isEmpty()) {
            restaurantInfoObject.add("mealVoucher",
                    JsonParser.parseString(
                            ResponseJSONBuilder.buildVouchersResponse(
                                    restaurantInfo.getMealVoucher(), user)
                    ));
        }

        baseJsonObject.add("restaurantInfo", restaurantInfoObject);
        return new Gson().toJson(baseJsonObject);
    }

    private static String buildMenuResponse(List<MenuEntry> menu) {

        JsonArray baseJsonObject = new JsonArray();

        for (MenuEntry entry : menu) {
            JsonObject entryObject = new JsonObject();
            entryObject.addProperty("itemName", entry.getItemName());
            entryObject.addProperty("category", entry.getCategory());
            entryObject.addProperty("description", entry.getDescription());
            entryObject.addProperty("price", entry.getPrice());
            entryObject.addProperty("currency", entry.getCurrency());
            baseJsonObject.add(entryObject);
        }

        return new Gson().toJson(baseJsonObject);
    }

    private static String buildReviewsResponse(List<Review> reviews) {

        JsonArray baseJsonObject = new JsonArray();

        for (Review review : reviews) {
            JsonObject reviewObject = new JsonObject();
            reviewObject.addProperty("user", review.getAppUser().getUsername());
            reviewObject.addProperty("restaurant", review.getRestaurantInfo().getRestaurant());
            reviewObject.addProperty("rating", review.getRating());
            reviewObject.addProperty("review", review.getRating());
            baseJsonObject.add(reviewObject);
        }
        return new Gson().toJson(baseJsonObject);
    }

    private static String buildVouchersResponse(List<MealVoucher> vouchers, String username) {

        JsonObject baseJsonObject = new JsonObject();

        for (MealVoucher voucher : vouchers) {
            if (voucher.getAppUser().getUsername().equals(username)) {
                baseJsonObject.addProperty("id", voucher.getId());
                baseJsonObject.addProperty("code", voucher.getCode());
                baseJsonObject.addProperty("description", voucher.getDescription());
            }
        }

        return new Gson().toJson(baseJsonObject);
    }
}
