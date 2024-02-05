package com.vieira.joao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vieira.joao.model.Review;

import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Integer> {
    void deleteReviewById(Integer id);

    Optional<Review> findReviewById(Integer id);
}
