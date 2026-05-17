package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetSitter;
import mk.petsitter.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);
        
        if (user instanceof PetOwner) {
            model.addAttribute("role", "OWNER");
        } else if (user instanceof PetSitter) {
            model.addAttribute("role", "SITTER");
        } else {
            model.addAttribute("role", "ADMIN");
        }
        
        return "dashboard";
    }
}