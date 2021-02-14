package com.udacity.jdnd.course3.critter.user.controller;

import com.udacity.jdnd.course3.critter.pet.service.PetService;
import com.udacity.jdnd.course3.critter.user.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.user.model.Customer;
import com.udacity.jdnd.course3.critter.user.model.Employee;
import com.udacity.jdnd.course3.critter.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * Handles web requests related to Users.
 * <p>
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    private final PetService petService;

    public UserController(UserService userService, PetService petService) {
        this.userService = userService;
        this.petService = petService;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer customer = convertToCustomer(customerDTO);
        if (customerDTO.getPetIds() != null)
            customer.setPets(customerDTO
                    .getPetIds()
                    .stream()
                    .map(petService::getPetById)
                    .collect(Collectors.toList()));
        return convertToCustomerDTO(userService.saveCustomer(customer));
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers() {
        return userService.getAllCustomers()
                .stream().map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) {
        return convertToCustomerDTO(userService.getOwnerByPetId(petId));
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return convertToEmployeeDTO(userService.saveEmployee(convertToEmployee(employeeDTO)));
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        return convertToEmployeeDTO(userService.getEmployeeById(employeeId));
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        userService.setEmployeeAvailability(daysAvailable, employeeId);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        return userService.getAllEmployeesForService(employeeDTO.getSkills(), employeeDTO.getDate())
                .stream()
                .map(this::convertToEmployeeDTO)
                .collect(Collectors.toList());
    }

    public Employee convertToEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        copyProperties(employeeDTO, employee);
        return employee;
    }

    public Customer convertToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        copyProperties(customerDTO, customer);
        return customer;
    }

    public EmployeeDTO convertToEmployeeDTO(Employee employee) {
        if (employee == null) return null;
        else {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            copyProperties(employee, employeeDTO);
            return employeeDTO;
        }
    }

    public CustomerDTO convertToCustomerDTO(Customer customer) {
        if (customer == null) return null;
        else {
            CustomerDTO customerDTO = new CustomerDTO();
            copyProperties(customer, customerDTO);
            return customerDTO;
        }
    }

}
