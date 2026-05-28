package mk.petsitter.service;

import java.util.List;
import mk.petsitter.model.Booking;
import mk.petsitter.model.Review;
import mk.petsitter.repository.BookingRepository;
import mk.petsitter.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Review addReview(String bookingId, Integer rating, String comment) {
        logger.info("Attempting to add review for booking ID: {}", bookingId);

        if (rating == null || rating < 1 || rating > 5) {
            logger.error("VALIDATION FAILED: Invalid rating: {}", rating);
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            logger.error("VALIDATION FAILED: Booking not found with ID: {}", bookingId);
            return new IllegalArgumentException("Invalid booking");
        });

        // 1. Validate Business Logic
        if (!"Completed".equalsIgnoreCase(booking.getStatus())) {
            logger.error("VALIDATION FAILED: Cannot review booking in status: {}", booking.getStatus());
            throw new IllegalStateException("Only completed bookings can be reviewed");
        }

        // 2. Create and save the review
        Review review = new Review();
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);
        
        Review savedReview = reviewRepository.save(review);
        logger.info("Successfully inserted review for booking: {}", bookingId);
        
        // 3. Update the booking status
        booking.setStatus("Reviewed");
        bookingRepository.save(booking);
        logger.info("Booking {} status successfully updated to Reviewed", bookingId);
        
        return savedReview;
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(String sitterId) {
        return reviewRepository.getAverageRatingForSitter(sitterId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsForSitter(String sitterId) {
        return reviewRepository.findByBooking_Sitter_UserId(sitterId);
    }

    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}