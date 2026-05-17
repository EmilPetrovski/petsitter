package mk.petsitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pet_types")
@Getter
@Setter
public class PetType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pettype_id", updatable = false, nullable = false, length = 36)
    private String pettypeId;

    @Column(nullable = false, unique = true, length = 128)
    private String species;

    @Column(name = "needs_outdoor_walk", nullable = false)
    private Boolean needsOutdoorWalk;
}