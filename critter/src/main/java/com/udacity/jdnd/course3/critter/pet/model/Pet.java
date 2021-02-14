package com.udacity.jdnd.course3.critter.pet.model;

import com.udacity.jdnd.course3.critter.user.model.Customer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Data
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private PetType type;
    private String name;
    private LocalDate birthDate;
    private String notes;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_fk")
    private Customer owner;

}
