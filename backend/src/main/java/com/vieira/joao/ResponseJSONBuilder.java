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

        JsonArray usersArray = new JsonArray();

        for (AppUser appUser : appUserList) {
            usersArray.add(appUser.getUsername());
        }

        return usersArray.toString();
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
            JsonArray voucherArray = JsonParser.parseString(
                    ResponseJSONBuilder.buildVouchersResponse(restaurantInfo.getMealVoucher(), user)).getAsJsonArray();

            if (!voucherArray.isEmpty()) {
                restaurantInfoObject.add("mealVouchers", voucherArray);
            }
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

    public static String buildReviewsResponse(List<Review> reviews) {

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

    public static String buildVouchersResponse(List<MealVoucher> vouchers, String username) {
        JsonArray vouchersList = new JsonArray();

        for (MealVoucher voucher : vouchers) {
            if (voucher.getAppUser().getUsername().equals(username)) {
                JsonObject voucherObject = new JsonObject();
                voucherObject.addProperty("id", voucher.getId());
                voucherObject.addProperty("code", voucher.getCode());
                voucherObject.addProperty("description", voucher.getDescription());
                vouchersList.add(voucherObject);
            }
        }

        return vouchersList.toString();
    }

    public static String buildErrorResponse(String reason) {
        JsonObject baseJsonObject = new JsonObject();
        baseJsonObject.addProperty("ErrorReason", reason);
        return baseJsonObject.toString();
    }
}
