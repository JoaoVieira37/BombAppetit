package sirs.a14.bombappetit.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sirs.a14.bombappetit.model.Review;
import sirs.a14.bombappetit.repository.ReviewRepo;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepo reviewRepo;

    @Autowired
    public ReviewService(ReviewRepo reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    public Review addReview(Review review) {
        return reviewRepo.save(review);
    }

    public Review updateReview(Review review) {
        return reviewRepo.save(review);
    }

    public List<Review> findAllReviews() {
        return reviewRepo.findAll();
    }

    public List<Review> findAllReviewsByRestaurant(String restaurant) {
        List<Review> reviewList = new ArrayList<>();
        List<Review> allReviews = reviewRepo.findAll();
        for (Review review : allReviews) {
            if (review.getRestaurantInfo().getRestaurant().equals(restaurant)) reviewList.add(review);
        }
        return reviewList;
    }

    public boolean existReviewsByRestaurant(String restaurant) {
        List<Review> reviewList = new ArrayList<>();
        List<Review> allReviews = reviewRepo.findAll();
        for (Review review : allReviews) {
            if (review.getRestaurantInfo().getRestaurant().equals(restaurant)) return true;
        }
        return false;
    }

    public Review findReviewById(Integer id) {
        return reviewRepo.findReviewById(id).orElseThrow();
    }

    public void deleteReviewById(Integer id) {
        reviewRepo.deleteReviewById(id);
    }
}


