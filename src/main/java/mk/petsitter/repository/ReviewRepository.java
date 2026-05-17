package mk.petsitter.repository;

import java.util.List;
import mk.petsitter.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, String> {
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.booking.sitter.userId = :sitterId")
    Double getAverageRatingForSitter(@Param("sitterId") String sitterId);

    List<Review> findByBooking_Sitter_UserId(String sitterId);
}