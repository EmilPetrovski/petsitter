package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import mk.petsitter.model.Pet;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.User;
import mk.petsitter.service.PetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PetController {
    
    private final PetService petService;
    
    public PetController(PetService petService) {
        this.petService = petService;
    }
    
    @GetMapping("/pets")
    public String showMyPets(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        List<Pet> pets = petService.getPetsByOwner(user.getUserId());
        model.addAttribute("pets", pets);
        return "my-pets";
    }
    
    @GetMapping("/pets/add")
    public String showAddPetForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("petTypes", petService.getAllPetTypes());
        return "add-pet";
    }
    
    @PostMapping("/pets/add")
    public String addPet(
            @RequestParam String name,
            @RequestParam Integer age,
            @RequestParam(required = false) String specialNeeds,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String photoUrl,
            @RequestParam String petTypeId,
            HttpSession session,
            Model model) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        
        petService.addPet(name, age, specialNeeds, description, photoUrl, user.getUserId(), petTypeId);
        return "redirect:/pets";
    }

    @GetMapping("/pets/edit")
    public String showEditPetForm(@RequestParam String petId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        
        petService.getPetsByOwner(user.getUserId()).stream()
            .filter(p -> p.getPetId().equals(petId))
            .findFirst()
            .ifPresent(pet -> model.addAttribute("pet", pet));
            
        return "edit-pet";
    }

    @PostMapping("/pets/edit")
    public String editPet(
            @RequestParam String petId,
            @RequestParam Integer age,
            @RequestParam(required = false) String specialNeeds,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String photoUrl,
            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        petService.updatePet(petId, user.getUserId(), age, specialNeeds, description, photoUrl);
        return "redirect:/pets";
    }

    @PostMapping("/pets/delete")
    public String deletePet(@RequestParam String petId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        petService.deletePet(petId, user.getUserId());
        return "redirect:/pets";
    }
}