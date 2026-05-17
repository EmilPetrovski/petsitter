package mk.petsitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "services")
@Getter
@Setter
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_id", updatable = false, nullable = false, length = 36)
    private String serviceId;

    @Column(nullable = false, length = 128)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;
}