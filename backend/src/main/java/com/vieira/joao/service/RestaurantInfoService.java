package com.vieira.joao.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vieira.joao.*;
import com.vieira.joao.model.AppUser;
import com.vieira.joao.repository.RestaurantInfoRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.vieira.joao.model.RestaurantInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
public class RestaurantInfoService {
    private final RestaurantInfoRepo restaurantInfoRepo;
    private final AppUserService appUserService;

    @Autowired
    public RestaurantInfoService(RestaurantInfoRepo restaurantInfoRepo, AppUserService appUserService) {
        this.restaurantInfoRepo = restaurantInfoRepo;
        this.appUserService = appUserService;
    }

    public List<RestaurantInfo> findAllRestaurantInfo() {
        return restaurantInfoRepo.findAll();
    }
    public String findAllRestaurantInfoJson() {
        return ResponseJSONBuilder.buildInfoResponse(findAllRestaurantInfo());
    }

    public RestaurantInfo findRestaurantInfoById(Integer id) {
        return restaurantInfoRepo.findRestaurantInfoById(id).orElseThrow();
    }

    public RestaurantInfo addRestaurantInfo(RestaurantInfo restaurantInfo) {
        return restaurantInfoRepo.save(restaurantInfo);
    }

    public RestaurantInfo updateRestaurantInfo(RestaurantInfo restaurantInfo) {
        return restaurantInfoRepo.save(restaurantInfo);
    }

    public void deleteRestaurantInfoById(Integer id) {
        restaurantInfoRepo.deleteRestaurantInfoById(id);
    }

    public String getRestaurantInfo(Integer id, String request) throws Exception {

        // Parse the request body to a JsonObject
        JsonObject requestBody = JsonParser.parseString(request).getAsJsonObject();

        // Get the username from the request body
        String username = requestBody.getAsJsonObject("info").get("username").getAsString();

        // Get the user's key path
        AppUser user = appUserService.findUserByUsername(username);
        String keyPath = user.getPublicKey();


        // TODO: Change the verify2 to verify after fixing all the methods relying on verify
        if (!VerifyClientJsonIntegrity.verify(requestBody, keyPath)) {
            return "{\"ERROR\":\"Nonce or Timestamp do not match\"}";
        }

        // Get the correct restaurant from the url
        RestaurantInfo restaurantInfo = findRestaurantInfoById(id);
        // Build the restaurantInfo Json
        JsonObject restaurantInfoObject = JsonParser.parseString(
                ResponseJSONBuilder.buildRestaurantInfoResponse(restaurantInfo, username)).getAsJsonObject();


        AuxFunctions.stringToJsonFile(restaurantInfoObject.toString(), "data.json");


        Protect.protectFind("data.json","data2.json",keyPath,"keys/serverPrivate.key");
        String content = new String(Files.readAllBytes(Paths.get("data2.json")));

        //DELETE DATA AND DATA2
        Files.deleteIfExists(Paths.get("data.json"));
        Files.deleteIfExists(Paths.get("data2.json"));



        return content;
    }

}
