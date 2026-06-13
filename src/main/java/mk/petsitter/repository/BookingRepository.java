package mk.petsitter.repository;

import mk.petsitter.model.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    
    // Get all bookings for a specific owner
    List<Booking> findByOwner_UserIdOrderByDateFromDesc(String ownerId);
    
    // Get all bookings assigned to a specific sitter
    List<Booking> findBySitter_UserIdOrderByDateFromDesc(String sitterId);

    @EntityGraph(attributePaths = {"owner", "sitter", "review", "payment"})
    @Query("SELECT b FROM Booking b")
    List<Booking> findAllWithDetails();
}