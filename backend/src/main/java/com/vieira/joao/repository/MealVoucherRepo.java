package com.vieira.joao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vieira.joao.model.MealVoucher;

import java.util.Optional;

public interface MealVoucherRepo extends JpaRepository<MealVoucher, Integer> {
    void deleteMealVoucherById(Integer id);

    Optional<MealVoucher> findMealVoucherById(Integer id);
}
