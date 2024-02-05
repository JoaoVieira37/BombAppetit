package com.vieira.joao.service;

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
