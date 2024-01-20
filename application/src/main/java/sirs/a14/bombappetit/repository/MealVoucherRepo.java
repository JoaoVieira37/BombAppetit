package sirs.a14.bombappetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sirs.a14.bombappetit.model.MealVoucher;

import java.util.Optional;

public interface MealVoucherRepo extends JpaRepository<MealVoucher, Integer> {
    void deleteMealVoucherById(Integer id);

    Optional<MealVoucher> findMealVoucherById(Integer id);
}
