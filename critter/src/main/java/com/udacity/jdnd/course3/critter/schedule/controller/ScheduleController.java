package com.udacity.jdnd.course3.critter.schedule.controller;

import com.udacity.jdnd.course3.critter.pet.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.pet.repository.PetRepository;
import com.udacity.jdnd.course3.critter.schedule.dto.ScheduleDTO;
import com.udacity.jdnd.course3.critter.schedule.model.Schedule;
import com.udacity.jdnd.course3.critter.schedule.service.ScheduleService;
import com.udacity.jdnd.course3.critter.user.exception.EmployeeNotFoundException;
import com.udacity.jdnd.course3.critter.user.repository.EmployeeRepository;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.*;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    private final PetRepository petRepository;

    private final EmployeeRepository employeeRepository;

    public ScheduleController(ScheduleService scheduleService, PetRepository petRepository, EmployeeRepository employeeRepository) {
        this.scheduleService = scheduleService;
        this.petRepository = petRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        return convertToScheduleDTO(scheduleService.createSchedule(convertToSchedule(scheduleDTO)));
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        return convertToListToScheduleDTO(scheduleService.getAllSchedules());
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        return convertToListToScheduleDTO(scheduleService.getScheduleForPetById(petId));
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return convertToListToScheduleDTO(scheduleService.getScheduleForEmployeeById(employeeId));
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        scheduleService.getScheduleForCustomerById(customerId)
                .forEach(e -> scheduleDTOS.addAll(convertToListToScheduleDTO(e)));
        return scheduleDTOS;
    }

    private Schedule convertToSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        copyProperties(scheduleDTO, schedule);

        scheduleDTO.getPetIds().stream()
                .map(petRepository::findById)
                .collect(Collectors.toList())
                .forEach(pet -> {
                    schedule.getPets().add(pet.orElseThrow(PetNotFoundException::new));
                });

        scheduleDTO.getEmployeeIds().stream()
                .map(employeeRepository::findById)
                .collect(Collectors.toList())
                .forEach(employee -> {
                    schedule.getEmployees().add(employee.orElseThrow(EmployeeNotFoundException::new));
                });
        return schedule;
    }

    private ScheduleDTO convertToScheduleDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        copyProperties(deepClone(schedule), scheduleDTO);
        schedule.getPets().forEach(pet -> scheduleDTO.getPetIds().add(pet.getId()));
        schedule.getEmployees().forEach(employee -> scheduleDTO.getEmployeeIds().add(employee.getId()));
        return scheduleDTO;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T deepClone(T object) {
        return (T) SerializationUtils.deserialize(SerializationUtils.serialize(object));
    }

    private List<ScheduleDTO> convertToListToScheduleDTO(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList());
    }
}
