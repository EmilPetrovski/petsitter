package mk.petsitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pets")
@Getter
@Setter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pet_id", updatable = false, nullable = false, length = 36)
    private String petId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "special_needs", columnDefinition = "TEXT")
    private String specialNeeds;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private PetOwner owner;

    @ManyToOne
    @JoinColumn(name = "pettype_id", nullable = false)
    private PetType petType;
}