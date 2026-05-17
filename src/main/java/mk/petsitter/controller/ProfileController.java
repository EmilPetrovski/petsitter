package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import mk.petsitter.model.User;
import mk.petsitter.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        
        User entity = userRepository.findById(user.getUserId()).orElseThrow();
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setEmail(email);
        userRepository.save(entity);
        session.setAttribute("loggedInUser", entity);
        return "redirect:/dashboard";
    }
}