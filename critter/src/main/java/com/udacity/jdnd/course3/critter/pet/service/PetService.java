package com.udacity.jdnd.course3.critter.pet.service;

import com.udacity.jdnd.course3.critter.pet.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.pet.model.Pet;
import com.udacity.jdnd.course3.critter.pet.repository.PetRepository;
import com.udacity.jdnd.course3.critter.user.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;

    private final CustomerRepository customerRepository;

    public PetService(PetRepository petRepository, CustomerRepository customerRepository) {
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
    }

    public Pet savePet(Pet pet) {
        pet = petRepository.save(pet);
        pet.getOwner().addPet(pet);
        customerRepository.save(pet.getOwner());
        return pet;
    }

    public Pet getPetById(Long Id) {
        return petRepository.findById(Id).orElseThrow(PetNotFoundException::new);
    }

    public List<Pet> getAllPetsByOwnerId(Long Id) {
        return petRepository.findAllByOwnerId(Id);
    }

    public List<Pet> getALlPets() {
        return petRepository.findAll();
    }
}
