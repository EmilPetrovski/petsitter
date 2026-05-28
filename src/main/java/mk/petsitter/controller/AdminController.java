package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mk.petsitter.model.Booking;
import mk.petsitter.model.User;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetSitter;
import mk.petsitter.model.Pet;
import mk.petsitter.model.Service;
import mk.petsitter.service.BookingService;
import mk.petsitter.service.PetService;
import mk.petsitter.service.ReviewService;
import mk.petsitter.service.UserService;
import mk.petsitter.repository.UserRepository;
import mk.petsitter.repository.ServiceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final PetService petService;
    private final ReviewService reviewService;
    private final ServiceRepository serviceRepository;
    private final UserService userService;

    public AdminController(BookingService bookingService, UserRepository userRepository, PetService petService, ReviewService reviewService, ServiceRepository serviceRepository, UserService userService) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.petService = petService;
        this.reviewService = reviewService;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
    }

    @GetMapping("/admin/bookings")
    public String showAllBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user instanceof PetOwner || user instanceof PetSitter) {
            return "redirect:/dashboard";
        }
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin-bookings";
    }
    
    @PostMapping("/admin/bookings/delete")
    public String deleteBooking(@RequestParam String bookingId, HttpSession session) {
        bookingService.deleteBooking(bookingId);
        return "redirect:/admin/bookings";
    }

    @GetMapping("/admin/users")
    public String showAllUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user instanceof PetOwner || user instanceof PetSitter) {
            return "redirect:/dashboard";
        }
        
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        
        Map<String, List<Pet>> ownerPets = new HashMap<>();
        Map<String, List<Service>> sitterServices = new HashMap<>();
        for (User u : users) {
            if (u instanceof PetOwner) {
                ownerPets.put(u.getUserId(), petService.getPetsByOwner(u.getUserId()));
            } else if (u instanceof PetSitter) {
                PetSitter sitter = (PetSitter) u;
                sitterServices.put(u.getUserId(), sitter.getOfferedServices());
            }
        }
        model.addAttribute("ownerPets", ownerPets);
        model.addAttribute("sitterServices", sitterServices);
        return "admin-users";
    }
    
    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam String userId, HttpSession session) {
        // Delegate multiple delete operations to a single transactional service method
        // This ensures bookings and pets are safely cleaned up before the user is deleted
        userService.deleteUserAccount(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/pets")
    public String showAllPets(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user instanceof PetOwner || user instanceof PetSitter) {
            return "redirect:/dashboard";
        }
        model.addAttribute("pets", petService.getAllPets());
        return "admin-pets";
    }

    @PostMapping("/admin/pets/delete")
    public String deletePetAdmin(@RequestParam String petId, HttpSession session) {
        petService.deletePetAdmin(petId);
        return "redirect:/admin/pets";
    }

    @GetMapping("/admin/reviews")
    public String showAllReviews(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user instanceof PetOwner || user instanceof PetSitter) {
            return "redirect:/dashboard";
        }
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin-reviews";
    }

    @PostMapping("/admin/reviews/delete")
    public String deleteReviewAdmin(@RequestParam String reviewId, HttpSession session) {
        reviewService.deleteReview(reviewId);
        return "redirect:/admin/reviews";
    }

    @GetMapping("/admin/services")
    public String showAllServices(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user instanceof PetOwner || user instanceof PetSitter) {
            return "redirect:/dashboard";
        }
        model.addAttribute("services", serviceRepository.findAll());
        return "admin-services";
    }

    @PostMapping("/admin/services/add")
    public String addService(@RequestParam String type, @RequestParam String description, HttpSession session) {
        Service service = new Service();
        service.setType(type);
        service.setDescription(description);
        serviceRepository.save(service);
        return "redirect:/admin/services";
    }

    @PostMapping("/admin/services/delete")
    public String deleteService(@RequestParam String serviceId, HttpSession session) {
        serviceRepository.deleteById(serviceId);
        return "redirect:/admin/services";
    }
}