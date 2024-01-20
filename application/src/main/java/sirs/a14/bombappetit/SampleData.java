package sirs.a14.bombappetit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import sirs.a14.bombappetit.model.*;
import sirs.a14.bombappetit.service.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SampleData implements ApplicationRunner {

    private final AppUserService appUserService;
    private final MenuEntryService menuEntryService;
    private final RestaurantInfoService restaurantInfoService;
    private final MealVoucherService mealVoucherService;
    private final ReviewService reviewService;

    @Autowired
    public SampleData(
            AppUserService appUserService,
            MenuEntryService menuEntryService,
            RestaurantInfoService restaurantInfoService,
            MealVoucherService mealVoucherService,
            ReviewService reviewService) {
        this.appUserService = appUserService;
        this.menuEntryService = menuEntryService;
        this.restaurantInfoService = restaurantInfoService;
        this.mealVoucherService = mealVoucherService;
        this.reviewService = reviewService;
    }

    public void run(final ApplicationArguments args) {
        addUsers();
        createRestaurants();

        //List<MealVoucher> mealVoucher = mealVoucherService.findAllMealVouchersByUser("user1");

        //SaveJson.saveMealVoucherListAsJson(mealVoucher);


        /*Review review = new Review();
        review.setReview("Show");
        review.setAppUser(appUserService.findUserByUsername("user1"));
        review.setRating(3);
        review.setRestaurantInfo(restaurantInfoService.findRestaurantInfoById(1));

        System.out.println(review);

        reviewService.addReview(review);*/


        /*List<MealVoucher> mealVoucherList = new ArrayList<>();
        List<RestaurantInfo> restaurantInfoList = restaurantInfoService.findAllRestaurantInfo();
        for (RestaurantInfo restaurantInfo : restaurantInfoList) {
            System.out.println("Number of Vouchers for Restaurante" + restaurantInfo.getRestaurant() + ": " + restaurantInfo.getMealVoucher().stream().count());
            if(!restaurantInfo.getMealVoucher().isEmpty()) {
                restaurantInfo.getMealVoucher().forEach(mealVoucher -> {
                    if (mealVoucher.getAppUser().getUsername().equals("user2")) {
                        mealVoucherList.add(mealVoucher);
                    }
                });
            }
        }
        if (!mealVoucherList.isEmpty()) {
            for (MealVoucher mealVoucher : mealVoucherList) {
                System.out.println(mealVoucher.getId());
                System.out.println(mealVoucher.getCode());
            }
        } else {
            System.out.println("#######################");
            System.out.println("#######################");
            System.out.println("null");
            System.out.println("#######################");
            System.out.println("#######################");
        }*/
    }

    private void addUsers() {
        if (appUserService.findAllUsers().stream().count() != 0) {
            return;
        }

        addUser("user1", "keys/user1Public.pub");
        addUser("user2", "keys/user2Public.pub");
        addUser("user3", "keys/user3Public.pub");
    }

    private void addUser(String username, String publicKeyPath) {
        AppUser user = new AppUser(username, publicKeyPath);
        appUserService.addUser(user);

    }

    private void createRestaurants() {
        createRestaurant1();
        createRestaurant2();
    }

    private void createRestaurant1() {

        // Create restaurant Info
        RestaurantInfo restaurantInfo = new RestaurantInfo(
                "Dona Maria",
                "Rua da Glória, 22, Lisboa",
                "Maria Silva",
                Arrays.asList("Portuguese", "Traditional")
        );
        // Save restaurant info on the Repo
        restaurantInfoService.addRestaurantInfo(restaurantInfo);

        // Create the menu Entries
        MenuEntry menuEntry1 = new MenuEntry(
                "House Steak",
                "Meat",
                "A succulent sirloin grilled steak.",
                24.99f,
                "EUR",
                restaurantInfo
        );
        MenuEntry menuEntry2 = new MenuEntry(
                "Sardines",
                "Fish",
                "A Portuguese staple, accompanied by potatoes and salad.",
                21.99f,
                "EUR",
                restaurantInfo
        );
        MenuEntry menuEntry3 = new MenuEntry(
                "Mushroom Risotto",
                "Vegetarian",
                "Creamy Arborio rice cooked with assorted mushrooms and Parmesan cheese.",
                16.99f,
                "EUR",
                restaurantInfo
        );

        // Save the menu entries on the db
        menuEntryService.addMenuEntry(menuEntry1);
        menuEntryService.addMenuEntry(menuEntry2);
        menuEntryService.addMenuEntry(menuEntry3);

        // Create meal vouchers
        MealVoucher mealVoucher1 = new MealVoucher(
                "VOUCHER123",
                "Redeem this code for a 20% discount in the meal. Drinks not included.",
                restaurantInfo,
                appUserService.findUserById(1)
        );
        MealVoucher mealVoucher2 = new MealVoucher(
                "VOUCHER123",
                "Redeem this code for a 20% discount in the meal. Drinks not included.",
                restaurantInfo,
                appUserService.findUserById(2)
        );

        // Save meal vouchers
        mealVoucherService.addMealVoucher(mealVoucher1);
        mealVoucherService.addMealVoucher(mealVoucher2);

        //  Add menu and meal vouchers to restaurant info
        restaurantInfo.setMenu(Arrays.asList(menuEntry1, menuEntry2, menuEntry3));
        restaurantInfo.setMealVoucher(Arrays.asList(mealVoucher1, mealVoucher2));

        // Update the old restaurant info
        restaurantInfoService.updateRestaurantInfo(restaurantInfo);
    }

    private void createRestaurant2() {

        RestaurantInfo restaurantInfo = new RestaurantInfo(
                "Kitchen From Hell",
                "Baker street, 221b, London",
                "Gordon Remsey",
                Arrays.asList("Gourmet", "Steakhouse")
        );

        restaurantInfoService.addRestaurantInfo(restaurantInfo);

        MenuEntry menuEntry1 = new MenuEntry(
                "Beef Wellington",
                "Meat",
                "A delicious beef short rib surrounded by thin slices of prosciuto serrano, envolved by a finely chopped mushroom duxelle and encased in beautifully flaky puff pastry.",
                49.99f,
                "GBP",
                restaurantInfo
        );
        MenuEntry menuEntry2 = new MenuEntry(
                "Lamb",
                "Meat",
                "Comes with sauce.",
                30.00f,
                "GBP",
                restaurantInfo
        );
        MenuEntry menuEntry3 = new MenuEntry(
                "Shepard's Pie",
                "Meat",
                "Just like me mum used to make.",
                20.00f,
                "GBP",
                restaurantInfo
        );
        MenuEntry menuEntry4 = new MenuEntry(
                "Garlic Bread",
                "Entree",
                "Bread with garlic, exploding with flavour, beautiful.",
                8.00f,
                "GBP",
                restaurantInfo
        );

        menuEntryService.addMenuEntry(menuEntry1);
        menuEntryService.addMenuEntry(menuEntry2);
        menuEntryService.addMenuEntry(menuEntry3);
        menuEntryService.addMenuEntry(menuEntry4);


        MealVoucher mealVoucher1 = new MealVoucher(
                "HALFOFF",
                "If bill is above 500GBP, you get half off one serving of garlic bread!.",
                restaurantInfo,
                appUserService.findUserById(2)
        );
        MealVoucher mealVoucher2 = new MealVoucher(
                "HALFOFF",
                "If bill is above 500GBP, you get half off one serving of garlic bread!.",
                restaurantInfo,
                appUserService.findUserById(3)
        );

        mealVoucherService.addMealVoucher(mealVoucher1);
        mealVoucherService.addMealVoucher(mealVoucher2);

        restaurantInfo.setMenu(Arrays.asList(menuEntry1, menuEntry2, menuEntry3));
        restaurantInfo.setMealVoucher(Arrays.asList(mealVoucher1, mealVoucher2));
        restaurantInfoService.updateRestaurantInfo(restaurantInfo);
    }

}
