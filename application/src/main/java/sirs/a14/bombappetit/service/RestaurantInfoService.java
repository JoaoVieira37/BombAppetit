package sirs.a14.bombappetit.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sirs.a14.bombappetit.model.RestaurantInfo;
import sirs.a14.bombappetit.repository.RestaurantInfoRepo;

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
