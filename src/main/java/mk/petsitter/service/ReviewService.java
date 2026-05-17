package mk.petsitter.service;

import java.util.List;
import mk.petsitter.model.Booking;
import mk.petsitter.model.Review;
import mk.petsitter.repository.BookingRepository;
import mk.petsitter.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Review addReview(String bookingId, Integer rating, String comment) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Invalid booking"));
        Review review = new Review();
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    public Double getAverageRating(String sitterId) {
        return reviewRepository.getAverageRatingForSitter(sitterId);
    }

    public List<Review> getReviewsForSitter(String sitterId) {
        return reviewRepository.findByBooking_Sitter_UserId(sitterId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}