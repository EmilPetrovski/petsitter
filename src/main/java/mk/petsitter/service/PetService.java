package mk.petsitter.service;

import java.util.List;

import mk.petsitter.model.Pet;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetType;
import mk.petsitter.repository.PetOwnerRepository;
import mk.petsitter.repository.PetRepository;
import mk.petsitter.repository.PetTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {
    private final PetRepository petRepository;
    private final PetTypeRepository petTypeRepository;
    private final PetOwnerRepository petOwnerRepository;

    public PetService(PetRepository petRepository, PetTypeRepository petTypeRepository, PetOwnerRepository petOwnerRepository) {
        this.petRepository = petRepository;
        this.petTypeRepository = petTypeRepository;
        this.petOwnerRepository = petOwnerRepository;
    }

    @Transactional(readOnly = true)
    public List<PetType> getAllPetTypes() {
        return petTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pet> getPetsByOwner(String ownerId) {
        return petRepository.findByOwner_UserId(ownerId);
    }

    @Transactional(readOnly = true)
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional
    public Pet addPet(String name, Integer age, String specialNeeds, String description, String photoUrl, String ownerId, String petTypeId) {
        PetOwner owner = petOwnerRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Invalid owner ID"));
        PetType petType = petTypeRepository.findById(petTypeId).orElseThrow(() -> new IllegalArgumentException("Invalid pet type ID"));
        
        Pet pet = new Pet();
        pet.setName(name);
        pet.setAge(age);
        pet.setSpecialNeeds(specialNeeds);
        pet.setDescription(description);
        pet.setPhoto(photoUrl);
        pet.setOwner(owner);
        pet.setPetType(petType);
        
        return petRepository.save(pet);
    }

    @Transactional
    public Pet updatePet(String petId, String ownerId, Integer age, String specialNeeds, String description, String photoUrl) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("Invalid pet ID"));
        if (!pet.getOwner().getUserId().equals(ownerId)) {
            throw new IllegalArgumentException("Not the owner");
        }
        pet.setAge(age);
        pet.setSpecialNeeds(specialNeeds);
        pet.setDescription(description);
        pet.setPhoto(photoUrl);
        
        return petRepository.save(pet);
    }

    @Transactional
    public void deletePet(String petId, String ownerId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("Invalid pet ID"));
        if (pet.getOwner().getUserId().equals(ownerId)) {
            petRepository.delete(pet);
        }
    }

    @Transactional
    public void deletePetAdmin(String petId) {
        petRepository.deleteById(petId);
    }
}