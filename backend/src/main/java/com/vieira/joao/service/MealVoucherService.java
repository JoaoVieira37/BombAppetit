package com.vieira.joao.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vieira.joao.ResponseJSONBuilder;
import com.vieira.joao.repository.MealVoucherRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vieira.joao.model.MealVoucher;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MealVoucherService {
    private final MealVoucherRepo mealVoucherRepo;

    @Autowired
    public MealVoucherService(MealVoucherRepo mealVoucherRepo) {
        this.mealVoucherRepo = mealVoucherRepo;
    }

    public MealVoucher addMealVoucher(MealVoucher mealVoucher) {
        return mealVoucherRepo.save(mealVoucher);
    }

    public MealVoucher updateMealVoucher(MealVoucher mealVoucher) {
        return mealVoucherRepo.save(mealVoucher);
    }

    public List<MealVoucher> findAllMealVouchers() {
        return mealVoucherRepo.findAll();
    }

    public List<MealVoucher> findAllMealVouchersByUser(String username) {
        List<MealVoucher> mealVouchers = mealVoucherRepo.findAll();
        List<MealVoucher> mealVouchersUser = new ArrayList<>();
        for (MealVoucher mealVoucher : mealVouchers) {
            if (mealVoucher.getAppUser().getUsername().equals(username)) {
                mealVouchersUser.add(mealVoucher);
            }
        }

        return mealVouchersUser;
    }

    /**
     * @param username
     * @return JsonObject in the format needed in protectVouchers:
     * {
     *   mealVouchers: [
     *      {
     *          "id": "1",
     *          "code": "VOUCHER123",
     *          "description" : "Some description",
     *          "restaurant_id": "1"
     *      }
     *   ]
     * }
     */
    public JsonObject findAllMealVouchersByUserAsJson(String username) {
        List<MealVoucher> mealVoucherList = findAllMealVouchersByUser(username);

        JsonObject root = new JsonObject();

        String voucherArray = ResponseJSONBuilder.buildVouchersResponse(mealVoucherList, username);
        JsonArray vouchersList = JsonParser.parseString(voucherArray).getAsJsonArray();

        for (JsonElement mealVoucher : vouchersList) {
            JsonObject mealVoucherObj = mealVoucher.getAsJsonObject();
            for (MealVoucher voucher : mealVoucherList) {
                if (voucher.getId() == mealVoucherObj.get("id").getAsInt()) {
                    mealVoucherObj.addProperty("restaurant_id", voucher.getRestaurantInfo().getId());
                }
            }

        }

        root.add("mealVouchers", vouchersList);

        return root;
    }

    public String findAllMealVouchersByUserAsJsonString(String username) {
        return findAllMealVouchersByUserAsJson(username).toString();
    }

    public boolean doesUserHaveVoucherForRestaurant(String username, String restaurant) {
        List<MealVoucher> mealVouchers = mealVoucherRepo.findAll();
        for (MealVoucher mealVoucher : mealVouchers) {
            if (mealVoucher.getAppUser().getUsername().equals(username) && mealVoucher.getRestaurantInfo().getRestaurant().equals(restaurant)) {
                return true;
            }
        }
        return false;
    }

    public Integer getVoucherIdForUserAndRestaurant(String username, String restaurant) {
        List<MealVoucher> mealVouchers = mealVoucherRepo.findAll();
        for (MealVoucher mealVoucher : mealVouchers) {
            if (mealVoucher.getAppUser().getUsername().equals(username) && mealVoucher.getRestaurantInfo().getRestaurant().equals(restaurant)) {
                return mealVoucher.getId();
            }
        }
        return 0;
    }

    public MealVoucher findMealVoucherById(Integer id) {
        return mealVoucherRepo.findMealVoucherById(id).orElseThrow();
    }

    public void deleteMealVoucherById(Integer id) {
        mealVoucherRepo.deleteMealVoucherById(id);
    }
}
