package sirs.a14.bombappetit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sirs.a14.bombappetit.model.Review;

import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Integer> {
    void deleteReviewById(Integer id);

    Optional<Review> findReviewById(Integer id);
}
