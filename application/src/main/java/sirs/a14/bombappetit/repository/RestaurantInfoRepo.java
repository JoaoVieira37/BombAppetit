package sirs.a14.bombappetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sirs.a14.bombappetit.model.RestaurantInfo;

import java.util.Optional;

public interface RestaurantInfoRepo extends JpaRepository<RestaurantInfo, Integer> {
    void deleteRestaurantInfoById(Integer id);

    Optional<RestaurantInfo> findRestaurantInfoById(Integer id);
}
