package com.vieira.joao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vieira.joao.model.RestaurantInfo;

import java.util.Optional;

public interface RestaurantInfoRepo extends JpaRepository<RestaurantInfo, Integer> {
    void deleteRestaurantInfoById(Integer id);

    Optional<RestaurantInfo> findRestaurantInfoById(Integer id);
}
