package com.vieira.joao.responses;

import com.vieira.joao.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsResponse {

    private List<String> reviewList;

    public ReviewsResponse(List<Review> reviews) {
        reviewList = new ArrayList<>();
        for (Review review : reviews) {
            this.reviewList.add(review.getRestaurantInfo().getRestaurant());
        }
    }

    public List<String> getReviews() {
        return reviewList;
    }

}
