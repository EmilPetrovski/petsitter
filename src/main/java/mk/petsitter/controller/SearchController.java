package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetSitter;
import mk.petsitter.model.User;
import mk.petsitter.repository.PetSitterRepository;
import mk.petsitter.repository.ServiceRepository;
import mk.petsitter.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    private final ServiceRepository serviceRepository;
    private final PetSitterRepository petSitterRepository;
    private final ReviewService reviewService;

    public SearchController(ServiceRepository serviceRepository, PetSitterRepository petSitterRepository, ReviewService reviewService) {
        this.serviceRepository = serviceRepository;
        this.petSitterRepository = petSitterRepository;
        this.reviewService = reviewService;
    }

    @GetMapping("/search")
    public String showSearchPage(
            @RequestParam(required = false) String serviceType,
            HttpSession session,
            Model model) {
        
        // Fetch all available services for the dropdown
        model.addAttribute("services", serviceRepository.findAll());
        
        if (serviceType != null && !serviceType.isEmpty()) {
            List<PetSitter> sitters = petSitterRepository.findByOfferedServices_Type(serviceType);
            model.addAttribute("sitters", sitters);
            model.addAttribute("selectedService", serviceType);
            
            Map<String, Double> ratings = new HashMap<>();
            for (PetSitter sitter : sitters) {
                Double avg = reviewService.getAverageRating(sitter.getUserId());
                ratings.put(sitter.getUserId(), avg != null ? avg : 0.0);
            }
            model.addAttribute("ratings", ratings);
        }

        return "search";
    }

    @GetMapping("/sitter")
    public String showSitterProfile(@RequestParam String sitterId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        
        petSitterRepository.findById(sitterId).ifPresent(sitter -> {
            model.addAttribute("sitter", sitter);
            Double avg = reviewService.getAverageRating(sitterId);
            model.addAttribute("averageRating", avg != null ? avg : 0.0);
            model.addAttribute("reviews", reviewService.getReviewsForSitter(sitterId));
        });
        return "sitter-profile";
    }
}