package mk.petsitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(nullable = false, unique = true, length = 254)
    private String email;
}