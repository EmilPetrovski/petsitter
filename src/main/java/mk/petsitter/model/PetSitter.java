package mk.petsitter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pet_sitters")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class PetSitter extends User {

    @ManyToMany
    @JoinTable(
        name = "sitter_services",
        joinColumns = @JoinColumn(name = "sitter_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> offeredServices;
}