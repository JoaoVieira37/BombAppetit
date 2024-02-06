package com.vieira.joao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vieira.joao.model.RestaurantInfo;

import java.util.List;

public class ResponseJSONBuilder {

    public static String buildInfoResponse(List<RestaurantInfo> restaurantInfoList) {

        Gson gson = new Gson();
        JsonObject baseJsonObject = new JsonObject();
        JsonArray restaurantInfoArray = new JsonArray();

        for (RestaurantInfo restaurantInfo : restaurantInfoList) {
            JsonObject restaurantInfoEntry = new JsonObject();
            restaurantInfoEntry.addProperty("id", restaurantInfo.getId());
            restaurantInfoEntry.addProperty("name", restaurantInfo.getRestaurant());
            restaurantInfoArray.add(restaurantInfoEntry);
        }

        baseJsonObject.add("restaurants", restaurantInfoArray);

        return gson.toJson(baseJsonObject);
    }

}