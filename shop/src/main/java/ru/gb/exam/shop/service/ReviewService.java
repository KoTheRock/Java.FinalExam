package ru.gb.exam.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gb.exam.shop.model.Product;    // Импорт Product
import ru.gb.exam.shop.model.Review;     // Импорт Review
import ru.gb.exam.shop.model.User;       // Импорт User
import ru.gb.exam.shop.repositories.ProductRepository; // Импорт ProductRepository
import ru.gb.exam.shop.repositories.ReviewRepository;  // Импорт ReviewRepository
import ru.gb.exam.shop.repositories.UserRepository;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private void updateAverageRating(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт с ID: " + productId + " не найден"));
        product.setAverageRating(averageRating);
        productRepository.save(product);
    }

    public Review addReview(Long productId, Review review, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Продукт с ID: " + productId + " не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID: " + userId + " не найден"));
        review.setProduct(product);
        review.setUser(user);
        Review savedReview = reviewRepository.save(review);
        updateAverageRating(productId);
        return savedReview;
    }

    public List<Review> getReviewByProducts(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Review updateReview(Long reviewId, Review updatedReview, Long userId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Отзыв с ID: " + reviewId + " не найден"));
        if (!existingReview.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Вы можете редактировать только свои отзывы");
        }
        existingReview.setComment(updatedReview.getComment());
        existingReview.setRating(updatedReview.getRating());
        Review savedReview = reviewRepository.save(existingReview);
        updateAverageRating(existingReview.getProduct().getId());
        return savedReview;
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Отзыв с ID: " + reviewId + " не найден"));
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Вы можете удалять только свои отзывы");
        }
        Long productId = review.getProduct().getId();
        reviewRepository.deleteById(reviewId);
        updateAverageRating(productId);
    }
}