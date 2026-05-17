package mk.petsitter.repository;

import mk.petsitter.model.PetSitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetSitterRepository extends JpaRepository<PetSitter, String> {
    
    List<PetSitter> findByOfferedServices_Type(String serviceType);
}