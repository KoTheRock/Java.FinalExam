package ru.gb.exam.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gb.exam.shop.model.Product;
import ru.gb.exam.shop.model.Review;
import ru.gb.exam.shop.model.User;
import ru.gb.exam.shop.repositories.ProductRepository;
import ru.gb.exam.shop.repositories.ReviewRepository;
import ru.gb.exam.shop.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private Product product;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pass1");
        userRepository.save(user1);

        user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pass2");
        userRepository.save(user2);

        product = new Product();
        product.setName("Smartphone");
        product.setPrice(new java.math.BigDecimal("1000.00"));
        productRepository.save(product);
    }

    @Test
    public void testAddReviewAndAverageRating() {
        Review review1 = new Review();
        review1.setComment("Great!");
        review1.setRating(5);

        Review review2 = new Review();
        review2.setComment("Good");
        review2.setRating(3);

        reviewService.addReview(product.getId(), review1, user1.getId());
        reviewService.addReview(product.getId(), review2, user1.getId());

        Product updatedProduct = productRepository.findById(product.getId()).get();
        assertEquals(4.0, updatedProduct.getAverageRating(), 0.01);
    }

    @Test
    public void testUpdateReviewSuccess() {
        Review review = new Review();
        review.setComment("OK");
        review.setRating(2);
        Review savedReview = reviewService.addReview(product.getId(), review, user1.getId());

        Review updatedReview = new Review();
        updatedReview.setComment("Better now");
        updatedReview.setRating(4);
        Review result = reviewService.updateReview(savedReview.getId(), updatedReview, user1.getId());

        assertEquals("Better now", result.getComment());
        assertEquals(4, result.getRating());
    }

    @Test
    public void testUpdateReviewForbidden() {
        Review review = new Review();
        review.setComment("OK");
        review.setRating(2);
        Review savedReview = reviewService.addReview(product.getId(), review, user1.getId());

        Review updatedReview = new Review();
        updatedReview.setComment("Trying to hack");
        updatedReview.setRating(5);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                reviewService.updateReview(savedReview.getId(), updatedReview, user2.getId())
        );
        assertEquals("Вы можете редактировать только свои отзывы", exception.getMessage());
    }

    @Test
    public void testDeleteReviewSuccess() {
        Review review = new Review();
        review.setComment("Delete me");
        review.setRating(3);
        Review savedReview = reviewService.addReview(product.getId(), review, user1.getId());

        reviewService.deleteReview(savedReview.getId(), user1.getId());
        assertFalse(reviewRepository.existsById(savedReview.getId()));
    }

    @Test
    public void testDeleteReviewForbidden() {
        Review review = new Review();
        review.setComment("Delete me");
        review.setRating(3);
        Review savedReview = reviewService.addReview(product.getId(), review, user1.getId());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                reviewService.deleteReview(savedReview.getId(), user2.getId())
        );
        assertEquals("Вы можете удалять только свои отзывы", exception.getMessage());
    }
}