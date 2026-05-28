package mk.petsitter.service;

import java.time.LocalDate;
import java.util.List;
import mk.petsitter.model.Booking;
import mk.petsitter.model.Pet;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetSitter;
import mk.petsitter.repository.BookingRepository;
import mk.petsitter.repository.PetOwnerRepository;
import mk.petsitter.repository.PetRepository;
import mk.petsitter.repository.PetSitterRepository;
import mk.petsitter.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;
    private final PetRepository petRepository;
    private final ServiceRepository serviceRepository;

    public BookingService(BookingRepository bookingRepository, PetOwnerRepository petOwnerRepository, PetSitterRepository petSitterRepository, PetRepository petRepository, ServiceRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.petSitterRepository = petSitterRepository;
        this.petRepository = petRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public Booking createBooking(String ownerId, String sitterId, LocalDate dateFrom, LocalDate dateTo, String address, List<String> petIds, String serviceType) {
        logger.info("Attempting to create booking. Owner: {}, Sitter: {}", ownerId, sitterId);

        // 1. Initial Validation
        if (dateFrom == null || dateTo == null || dateFrom.isAfter(dateTo)) {
            logger.error("VALIDATION FAILED: Invalid date range provided");
            throw new IllegalArgumentException("Booking dates are invalid");
        }
        if (petIds == null || petIds.isEmpty()) {
            logger.error("VALIDATION FAILED: No pets selected for booking");
            throw new IllegalArgumentException("At least one pet must be selected");
        }

        // 2. Fetch Entities
        PetOwner owner = petOwnerRepository.findById(ownerId).orElseThrow(() -> {
            logger.error("VALIDATION FAILED: Owner not found with ID: {}", ownerId);
            return new IllegalArgumentException("Invalid owner");
        });
        
        PetSitter sitter = petSitterRepository.findById(sitterId).orElseThrow(() -> {
            logger.error("VALIDATION FAILED: Sitter not found with ID: {}", sitterId);
            return new IllegalArgumentException("Invalid sitter");
        });
        
        List<Pet> pets = petRepository.findAllById(petIds);
        if (pets.size() != petIds.size()) {
            logger.error("VALIDATION FAILED: One or more pet IDs do not exist in the database");
            throw new IllegalArgumentException("Invalid pets selected");
        }

        // 3. Entity Construction
        Booking booking = new Booking();
        booking.setOwner(owner);
        booking.setSitter(sitter);
        booking.setDateFrom(dateFrom);
        booking.setDateTo(dateTo);
        booking.setAddress(address);
        booking.setPets(pets);
        
        if (serviceType != null && !serviceType.isEmpty()) {
            serviceRepository.findAll().stream()
                .filter(s -> s.getType().equals(serviceType))
                .findFirst()
                .ifPresent(service -> booking.setServices(List.of(service)));
        }
        
        // 4. Write Operation
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Successfully created booking {} for owner {}", savedBooking.getBookingId(), owner.getUsername());
        return savedBooking;
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsForSitter(String sitterId) {
        return bookingRepository.findBySitter_UserIdOrderByDateFromDesc(sitterId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsForOwner(String ownerId) {
        return bookingRepository.findByOwner_UserIdOrderByDateFromDesc(ownerId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public void updateBookingStatus(String bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking"));
        booking.setStatus(status);
    }

    @Transactional
    public void deleteBooking(String bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}