package mk.petsitter.repository;

import mk.petsitter.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, String> {
    
    // Finds all pets belonging to a specific owner
    List<Pet> findByOwner_UserId(String ownerId);
}