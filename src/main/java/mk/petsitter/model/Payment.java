package mk.petsitter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", updatable = false, nullable = false, length = 36)
    private String paymentId;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "payment_type", nullable = false, length = 32)
    private String paymentType;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;
}