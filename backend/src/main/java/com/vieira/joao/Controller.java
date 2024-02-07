package com.vieira.joao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vieira.joao.model.AppUser;
import com.vieira.joao.model.MealVoucher;
import com.vieira.joao.model.RestaurantInfo;
import com.vieira.joao.model.Review;
import com.vieira.joao.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // TODO: find/id -> should also return review after adding one / should make sure the id is valid
    // TODO: vouchers -> to know the vouchers available to give to another user / should send encrypted json - client should decrypt
    // TODO: give/id -> change app user of voucher with id id
    // TODO: Delete temporary files left behind by the functions


    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(restaurantInfoService.findAllRestaurantInfoJson());
    }

    @GetMapping("/users")
    public ResponseEntity<String> getUsers() {
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(appUserService.findAllUsersJson());
    }

    // TODO: Reviews (GET Method)
    /*@GetMapping("/reviews")
    public ResponseEntity<String> getReviews() {
        List<Review> reviewList = reviewService.findAllReviewsByRestaurant("Dona Maria");
        ReviewsResponse response = new ReviewsResponse(reviewList);

        Gson gson = new Gson();
        String json = gson.toJson(response);
        return ResponseEntity.ok().body(json);
    }*/

    @PostMapping("/find/{id}")
    public ResponseEntity<String> getRestaurantInfo(@PathVariable("id") Integer id, @RequestBody String json) throws Exception {

        Gson gson = new Gson();
        JsonObject requestBody = JsonParser.parseString(json).getAsJsonObject();

        /*
         *
         * {"info":
         * {
         * "username":"user1",
         * "timestamp":"1707237363417"
         * }
         * }
         *
         * */

        AuxFunctions.stringToJsonFile(json, "data.json");

        // Get the username from the request body
        String username = requestBody.getAsJsonObject("info").get("username").getAsString();
        // Get the user with username from the db
        AppUser user = appUserService.findUserByUsername(username);
        // Get the corresponding key path
        String keyPath = user.getPublicKey();

        // What does this do?
        if (!VerifyClientJsonIntegrity.verify("data.json", keyPath)) {
            return ResponseEntity.ok().body("{\"ERROR\":\"Nonce or Timestamp do not match\"}");
        }

        //Files.deleteIfExists(Paths.get("data.json"));

        RestaurantInfo restaurantInfo = restaurantInfoService.findRestaurantInfoById(id);

        JsonObject jsonObject = new JsonObject();

        JsonObject restaurantInfoObject = JsonParser.parseString(ResponseJSONBuilder.buildRestaurantInfoResponse(restaurantInfo, username)).getAsJsonObject();

        Debug.debug(restaurantInfoObject.getAsJsonObject("restaurantInfo").get("restaurant").getAsString());

        /*if (mealVoucherService.doesUserHaveVoucherForRestaurant(username,
                restaurantInfoObject.getAsJsonObject("restaurantInfo").get("restaurant").getAsString())){
            Integer i = mealVoucherService.getVoucherIdForUserAndRestaurant(username,restaurantInfoObject.getAsJsonObject("restaurantInfo").get("restaurant").getAsString());
            System.out.println(i);
            MealVoucher mv = mealVoucherService.findMealVoucherById(i);
            JsonObject mvjson = new JsonParser().parse(mv.toString()).getAsJsonObject();
            restaurantInfoObject.add("mealVoucher", mvjson);
        }*/

        /*if (reviewService.existReviewsByRestaurant(restaurantInfoObject.get("restaurant").getAsString())){
            Debug.debug(restaurantInfo.getReviews().size());
            List<Review> reviews = reviewService.findAllReviewsByRestaurant(restaurantInfoObject.get("restaurant").getAsString());
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
            restaurantInfoObject.add("reviews", reviewArray);
        }*/
        jsonObject.add("restaurantInfo", restaurantInfoObject);
        AuxFunctions.stringToJsonFile(jsonObject.toString(), "data.json");
        Protect.protectFind("data.json","data2.json",keyPath,"keys/serverPrivate.key");
        String content = new String(Files.readAllBytes(Paths.get("data2.json")));
        System.out.println(content); 

        //DELETE DATA AND DATA2
        Files.deleteIfExists(Paths.get("data.json"));
        Files.deleteIfExists(Paths.get("data2.json"));

        return ResponseEntity.ok().body(content);

    }

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
            restaurantInfo.addReview(review);
            restaurantInfoService.updateRestaurantInfo(restaurantInfo);
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
