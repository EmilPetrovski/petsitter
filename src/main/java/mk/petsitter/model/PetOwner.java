package mk.petsitter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pet_owners")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class PetOwner extends User {
}