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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
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
        PetOwner owner = petOwnerRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Invalid owner"));
        PetSitter sitter = petSitterRepository.findById(sitterId).orElseThrow(() -> new IllegalArgumentException("Invalid sitter"));
        List<Pet> pets = petRepository.findAllById(petIds);

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
        
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsForSitter(String sitterId) {
        return bookingRepository.findBySitter_UserIdOrderByDateFromDesc(sitterId);
    }

    public List<Booking> getBookingsForOwner(String ownerId) {
        return bookingRepository.findByOwner_UserIdOrderByDateFromDesc(ownerId);
    }

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