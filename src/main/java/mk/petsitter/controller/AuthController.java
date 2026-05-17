package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import mk.petsitter.model.User;
import mk.petsitter.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        // Renders the login.html template
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username, 
            @RequestParam String password, 
            HttpSession session, 
            Model model) {
        
        User user = userService.authenticate(username, password);
        if (user != null) {
            // Start authenticated session
            session.setAttribute("loggedInUser", user);
            return "redirect:/dashboard"; 
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String username, @RequestParam String password,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String role,
            Model model) {
        try {
            userService.registerUser(username, password, firstName, lastName, email, role);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}