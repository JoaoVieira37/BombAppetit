package com.vieira.joao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.vieira.joao.model.AppUser;
import com.vieira.joao.model.MealVoucher;
import com.vieira.joao.model.RestaurantInfo;
import com.vieira.joao.model.Review;
import com.vieira.joao.responses.InfoResponse;
import com.vieira.joao.responses.InfoResponseWrapper;
import com.vieira.joao.responses.UserResponse;
import com.vieira.joao.responses.UserResponseWrapper;
import com.vieira.joao.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sirs.a14.bombappetit.responses.*;
import sirs.a14.bombappetit.service.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class Controller {
    private final RestaurantInfoService restaurantInfoService;
    private final AppUserService appUserService;
    private final MenuEntryService menuEntryService;
    private final ReviewService reviewService;
    private final MealVoucherService mealVoucherService;

    public Controller(RestaurantInfoService restaurantInfoService, AppUserService appUserService, MenuEntryService menuEntryService, ReviewService reviewService, MealVoucherService mealVoucherService) {
        this.restaurantInfoService = restaurantInfoService;
        this.appUserService = appUserService;
        this.menuEntryService = menuEntryService;
        this.reviewService = reviewService;
        this.mealVoucherService = mealVoucherService;
    }

    // TODO: info -> done
    // TODO: find/id -> should also return review after adding one / should make sure the id is valid
    // TODO: users -> done
    // TODO: review -> needs more testing
    // TODO: vouchers -> to know the vouchers available to give to another user / should send encrypted json - client should decrypt
    // TODO: give/id -> change app user of voucher with id id
    // TODO: Delete temporary files left behind by the functions


    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        List<RestaurantInfo> restaurantInfoList = restaurantInfoService.findAllRestaurantInfo();
        List<InfoResponse> infoResponseList = new ArrayList<>();
        for (RestaurantInfo restaurantInfo : restaurantInfoList) {
            infoResponseList.add(new InfoResponse(restaurantInfo.getId(), restaurantInfo.getRestaurant()));
        }
        InfoResponseWrapper infoResponseWrapper = new InfoResponseWrapper(infoResponseList);

        Gson gson = new Gson();
        String json = gson.toJson(infoResponseWrapper);

        return ResponseEntity.ok().body(json);
    }

    @PostMapping("/find/{id}")
    public ResponseEntity<String> getRestaurantInfo(@PathVariable("id") Integer id, @RequestBody String json) throws Exception {
        

        AuxFunctions.stringToJsonFile(json, "data.json");

        String username = AuxFunctions.getFieldFromJson("info", "username", "data.json");

        AppUser user = appUserService.findUserByUsername(username);
        String keypath = user.getPublicKey();
        System.out.println(keypath);

        if (!VerifyClientJsonIntegrity.verify("data.json", keypath)) {
            return ResponseEntity.ok().body("{\"ERROR\":\"Nonce or Timestamp do not match\"}");
        }

        //Files.deleteIfExists(Paths.get("data.json"));

        RestaurantInfo restaurantInfo = restaurantInfoService.findRestaurantInfoById(id);

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        JsonObject jsonObj = new JsonParser().parse(restaurantInfo.toString()).getAsJsonObject();

        if (mealVoucherService.doesUserHaveVoucherForRestaurant(username,jsonObj.get("restaurant").getAsString())){
            Integer i = mealVoucherService.getVoucherIdForUserAndRestaurant(username,jsonObj.get("restaurant").getAsString());
            System.out.println(i);
            MealVoucher mv = mealVoucherService.findMealVoucherById(i);
            JsonObject mvjson = new JsonParser().parse(mv.toString()).getAsJsonObject();
            jsonObj.add("mealVoucher", mvjson);
        }

        if (reviewService.existReviewsByRestaurant(jsonObj.get("restaurant").getAsString())){
            List<Review> reviews = reviewService.findAllReviewsByRestaurant(jsonObj.get("restaurant").getAsString());
            System.out.println(reviews.toString());
            JsonArray reviewArray = new JsonArray();
            for (Review review : reviews) {
                JsonObject reviewObject = new JsonObject();
                reviewObject.addProperty("user", review.getAppUser().getUsername());
                reviewObject.addProperty("restaurant", review.getRestaurantInfo().getRestaurant());
                reviewObject.addProperty("rating", review.getRating());
                reviewObject.addProperty("review", review.getReview());
                reviewArray.add(reviewObject);
            }
            jsonObj.add("reviews", reviewArray);
        }
        jsonObject.add("restaurantInfo", jsonObj);
        AuxFunctions.stringToJsonFile(jsonObject.toString(), "data.json");
        Protect.protectFind("data.json","data2.json",keypath,"keys/serverPrivate.key");
        String content = new String(Files.readAllBytes(Paths.get("data2.json")));
        System.out.println(content); 

        //DELETE DATA AND DATA2
        Files.deleteIfExists(Paths.get("data.json"));
        Files.deleteIfExists(Paths.get("data2.json"));

        return ResponseEntity.ok().body(content);

    }

    @GetMapping("/users")
    public ResponseEntity<String> getUsers() {
        List<AppUser> appUserList = appUserService.findAllUsers();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (AppUser appUser : appUserList) {
            userResponseList.add(new UserResponse(appUser.getId(), appUser.getUsername()));
        }
        UserResponseWrapper userResponseWrapper = new UserResponseWrapper(userResponseList);

        Gson gson = new Gson();
        String json = gson.toJson(userResponseWrapper);

        return ResponseEntity.ok().body(json);
    }

    /*@GetMapping("/reviews")
    public ResponseEntity<String> getReviews() {
        List<Review> reviewList = reviewService.findAllReviewsByRestaurant("Dona Maria");
        ReviewsResponse response = new ReviewsResponse(reviewList);

        Gson gson = new Gson();
        String json = gson.toJson(response);
        return ResponseEntity.ok().body(json);
    }*/

    @PostMapping("/add/review")
    public ResponseEntity<String> addReview(@RequestBody String json) throws Exception {
        System.out.println(json);
        AuxFunctions.stringToJsonFile(json, "data.json");

        // Parse the JSON to get the username
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        String username = jsonNode.get("info").get("username").asText();
        Integer id = jsonNode.get("info").get("restaurantID").asInt();

        // Ir buscar o path da key a DB
        String publicKeyPath = appUserService.findUserByUsername(username).getPublicKey();

        if (VerifyClientJsonIntegrity.verify("data.json", publicKeyPath)) {
            String reviewText = jsonNode.get("info").get("review").asText();
            Integer rating = jsonNode.get("info").get("rating").asInt();

            AppUser user = appUserService.findUserByUsername(username);
            RestaurantInfo restaurantInfo = restaurantInfoService.findRestaurantInfoById(id);

            // Create a new review and save it
            Review review = new Review();
            review.setReview(reviewText);
            review.setRestaurantInfo(restaurantInfo);
            review.setAppUser(user);
            review.setRating(rating);
            reviewService.addReview(review);
            Files.deleteIfExists(Path.of("data.json"));
            String response = "Review added successfully";
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Files.deleteIfExists(Path.of("data.json"));
            String response = "Review not added";
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    @PostMapping("/vouchers")
    public ResponseEntity<String> getVouchers(@RequestBody String json) throws Exception {
        System.out.println(json);
        AuxFunctions.stringToJsonFile(json, "data.json");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        String username = jsonNode.get("info").get("username").asText();

        String publicPathKey = appUserService.findUserByUsername(username).getPublicKey();

        List<MealVoucher> mealVoucher = mealVoucherService.findAllMealVouchersByUser(username);


        if (VerifyClientJsonIntegrity.verify("data.json", publicPathKey)) {

            SaveJson.saveMealVoucherListAsJson(mealVoucher);

            AppUser user = appUserService.findUserByUsername(username);
            try {
                Protect.protectVouchers("voucher.json", "encrypted-vouchers.json", publicPathKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String content = new String(Files.readAllBytes(Path.of("encrypted-vouchers.json")));
            System.out.println(content);


            //DELETE DATA AND DATA2
            Files.deleteIfExists(Path.of("data.json"));
            Files.deleteIfExists(Path.of("voucher.json"));
            Files.deleteIfExists(Path.of("encrypted-vouchers.json"));



            return ResponseEntity.ok().body(content);
        } else {

            Files.deleteIfExists(Path.of("data.json"));
            String response = "Review not added";
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/give")
    public ResponseEntity<String> giveVoucher(@RequestBody String json) throws Exception {
        System.out.println(json);
        AuxFunctions.stringToJsonFile(json, "data.json");

        String username = AuxFunctions.getFieldFromJson("info", "username", "data.json");
        AppUser user = appUserService.findUserByUsername(username);
        String keypath = user.getPublicKey();

        if (VerifyClientJsonIntegrity.verify("data.json", keypath)) {
            String targetusername = AuxFunctions.getFieldFromJson("info", "targetuser", "data.json");
            String voucherId = AuxFunctions.getFieldFromJson("info", "voucherID", "data.json");
            Integer vid = Integer.valueOf(voucherId);

            AppUser targetuser = appUserService.findUserByUsername(targetusername);
            MealVoucher mealVoucher = mealVoucherService.findMealVoucherById(vid);
            mealVoucher.setAppUser(targetuser);
            mealVoucherService.updateMealVoucher(mealVoucher);

            Files.deleteIfExists(Path.of("data.json"));

            String response = "Given successfully";
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{

            Files.deleteIfExists(Path.of("data.json"));
            String response = "Not given";
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

}
