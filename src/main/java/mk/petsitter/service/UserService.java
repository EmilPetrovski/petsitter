package mk.petsitter.service;

import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetSitter;
import mk.petsitter.model.User;
import mk.petsitter.repository.PetOwnerRepository;
import mk.petsitter.repository.PetSitterRepository;
import mk.petsitter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;

    public UserService(UserRepository userRepository, PetOwnerRepository petOwnerRepository, PetSitterRepository petSitterRepository) {
        this.userRepository = userRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.petSitterRepository = petSitterRepository;
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
}