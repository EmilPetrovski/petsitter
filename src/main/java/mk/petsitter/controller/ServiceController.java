package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mk.petsitter.model.PetSitter;
import mk.petsitter.model.Service;
import mk.petsitter.model.User;
import mk.petsitter.repository.PetSitterRepository;
import mk.petsitter.repository.ServiceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ServiceController {
    private final PetSitterRepository petSitterRepository;
    private final ServiceRepository serviceRepository;

    public ServiceController(PetSitterRepository petSitterRepository, ServiceRepository serviceRepository) {
        this.petSitterRepository = petSitterRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/services")
    public String showServices(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetSitter)) return "redirect:/dashboard";
        
        model.addAttribute("allServices", serviceRepository.findAll());
        petSitterRepository.findById(user.getUserId()).ifPresent(sitter -> {
            List<String> myIds = sitter.getOfferedServices().stream().map(Service::getServiceId).collect(Collectors.toList());
            model.addAttribute("myServiceIds", myIds);
        });
        return "sitter-services";
    }

    @PostMapping("/services/update")
    public String updateServices(@RequestParam(required = false) List<String> serviceIds, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetSitter)) return "redirect:/dashboard";
        
        PetSitter sitter = petSitterRepository.findById(user.getUserId()).orElseThrow();
        List<Service> selected = serviceIds == null ? new ArrayList<>() : serviceRepository.findAllById(serviceIds);
        sitter.setOfferedServices(selected);
        petSitterRepository.save(sitter);
        return "redirect:/dashboard";
    }
}