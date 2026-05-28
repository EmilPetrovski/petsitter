package mk.petsitter.service;

import java.util.List;
import mk.petsitter.model.Booking;
import mk.petsitter.model.Pet;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetSitter;
import mk.petsitter.model.User;
import mk.petsitter.repository.PetOwnerRepository;
import mk.petsitter.repository.PetSitterRepository;
import mk.petsitter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;
    private final BookingService bookingService;
    private final PetService petService;

    public UserService(UserRepository userRepository, PetOwnerRepository petOwnerRepository, PetSitterRepository petSitterRepository, BookingService bookingService, PetService petService) {
        this.userRepository = userRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.petSitterRepository = petSitterRepository;
        this.bookingService = bookingService;
        this.petService = petService;
    }

    @Transactional(readOnly = true)
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        // P9 - use BCryptPasswordEncoder here
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Transactional
    public User registerUser(String username, String password, String firstName, String lastName, String email, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        User newUser;
        if ("OWNER".equalsIgnoreCase(role)) {
            newUser = new PetOwner();
        } else if ("SITTER".equalsIgnoreCase(role)) {
            newUser = new PetSitter();
        } else {
            throw new IllegalArgumentException("Invalid role selected!");
        }

        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);

        // Saving a PetOwner or PetSitter automatically saves to the parent 'users' table with InhertianceType.JOINED
        if (newUser instanceof PetOwner) {
            return petOwnerRepository.save((PetOwner) newUser);
        } else {
            return petSitterRepository.save((PetSitter) newUser);
        }
    }

    @Transactional
    public void deleteUserAccount(String userId) {
        logger.info("Attempting to securely delete user account with ID: {}", userId);
        
        // 1. Fetch and delete all bookings associated with the user
        List<Booking> ownerBookings = bookingService.getBookingsForOwner(userId);
        ownerBookings.forEach(b -> bookingService.deleteBooking(b.getBookingId()));
        
        List<Booking> sitterBookings = bookingService.getBookingsForSitter(userId);
        sitterBookings.forEach(b -> bookingService.deleteBooking(b.getBookingId()));
        
        // 2. Delete all pets owned by this user
        List<Pet> pets = petService.getPetsByOwner(userId);
        pets.forEach(p -> petService.deletePetAdmin(p.getPetId()));
        
        // Uncomment this temporarily for testing, validation for deletion
        // if (true) {
        //     throw new RuntimeException("Simulated Server Crash!");
        // }
        
        // 3. Delete the user finally
        userRepository.deleteById(userId);
        logger.info("Successfully deleted user account and all related data for ID: {}", userId);
    }
}