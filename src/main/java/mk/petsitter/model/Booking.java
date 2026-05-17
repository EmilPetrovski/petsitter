package mk.petsitter.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id", updatable = false, nullable = false, length = 36)
    private String bookingId;

    @Column(nullable = false, length = 32)
    private String status = "Pending";

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;

    @Column(nullable = false, length = 512)
    private String address;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private PetOwner owner;

    @ManyToOne
    @JoinColumn(name = "sitter_id", nullable = false)
    private PetSitter sitter;

    @ManyToMany
    @JoinTable(
        name = "booking_pets",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "pet_id")
    )
    private List<Pet> pets;

    @ManyToMany
    @JoinTable(
        name = "booking_services",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;

    @OneToOne(mappedBy = "booking")
    private Review review;

    @OneToOne(mappedBy = "booking")
    private Payment payment;
}