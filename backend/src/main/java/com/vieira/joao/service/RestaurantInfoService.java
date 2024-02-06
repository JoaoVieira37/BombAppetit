package com.vieira.joao.service;

import com.vieira.joao.ResponseJSONBuilder;
import com.vieira.joao.repository.RestaurantInfoRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vieira.joao.model.RestaurantInfo;

import java.util.List;

@Service
@Transactional
public class RestaurantInfoService {
    private final RestaurantInfoRepo restaurantInfoRepo;

    @Autowired
    public RestaurantInfoService(RestaurantInfoRepo restaurantInfoRepo) {
        this.restaurantInfoRepo = restaurantInfoRepo;
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
}
