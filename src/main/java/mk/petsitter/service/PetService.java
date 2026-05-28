package mk.petsitter.service;

import java.util.List;

import mk.petsitter.model.Pet;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetType;
import mk.petsitter.repository.PetOwnerRepository;
import mk.petsitter.repository.PetRepository;
import mk.petsitter.repository.PetTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {
    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

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
        logger.info("Attempting to add pet '{}' for owner ID: {}", name, ownerId);

        if (name == null || name.isBlank()) {
            logger.error("VALIDATION FAILED: Pet name is null or empty");
            throw new IllegalArgumentException("Pet name is strictly required");
        }

        // 1. Read/Verify Owner exists
        PetOwner owner = petOwnerRepository.findById(ownerId).orElseThrow(() -> {
            logger.error("VALIDATION FAILED: Owner not found with ID: {}", ownerId);
            return new IllegalArgumentException("Invalid owner ID");
        });
        
        // 2. Read/Verify PetType exists
        PetType petType = petTypeRepository.findById(petTypeId).orElseThrow(() -> {
            logger.error("VALIDATION FAILED: Pet type not found with ID: {}", petTypeId);
            return new IllegalArgumentException("Invalid pet type ID");
        });
        
        Pet pet = new Pet();
        pet.setName(name);
        pet.setAge(age);
        pet.setSpecialNeeds(specialNeeds);
        pet.setDescription(description);
        pet.setPhoto(photoUrl);
        pet.setOwner(owner);
        pet.setPetType(petType);
        
        // 3. Insert the new Pet
        Pet savedPet = petRepository.save(pet);
        logger.info("Successfully added pet '{}' under owner '{}'", savedPet.getName(), owner.getUsername());
        
        return savedPet;
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