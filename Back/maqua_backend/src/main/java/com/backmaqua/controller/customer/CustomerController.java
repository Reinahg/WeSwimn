package com.backmaqua.controller.customer;

import java.net.URI;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;

import com.backmaqua.entities.customer.Customer;
import com.backmaqua.entities.customer.Customers;
import com.backmaqua.entities.user.User;
import com.backmaqua.repository.customer.CustomerCRUDRepository;
import com.backmaqua.repository.user.UserCRUDRepository;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/customers")
 public class CustomerController {

	@Autowired
	private CustomerCRUDRepository customerRepository;
	
	@Autowired
	private UserCRUDRepository userRepository;
	
	@Value("${customerRoleId}") // Inyecta el valor del rol desde application.properties
    private Long customerRoleId;
	
	@CrossOrigin(origins = "*")
    @PostMapping(path= "/addNewCustomer", consumes = "application/json", produces = "application/json")
	public Customer addNewCustomerApi(@RequestBody Customer customer) {
        //add resource
		customer = customerRepository.save(customer);
	    // Crear un usuario correspondiente al profesor
        User user = new User();
        user.setUser(customer.getName()); // Usar el nombre del cliente como nombre de usuario
        user.setPassword(customer.getPassword()); // Define una contraseña segura aquí
        user.setRolId(customerRoleId); // Puedes asignar un rol específico para cliente (por ejemplo, 1).

        // Guardar el usuario
        userRepository.save(user);
        
        // Establecer el mismo id para el cliente y el usuario
        customer.setUserId(user.getId());
        
        //Actualizar cliente
        customerRepository.save(customer);
		return customer;
	}
	
	@GetMapping(path = "getCustomerById", produces = "application/json")
	public Customer getUserById(@RequestParam(value="id") Long id) {
	    Customer customer = customerRepository.findById(id).get();
	    return customer;
	}
	
    //***Api Final Para FRONT
	@CrossOrigin(origins = "*")
    @GetMapping(path= "/customergetall", produces = "application/json")
    public Customers getAllEmployeesApi() 
    {
		Customers response = new Customers();
		ArrayList<Customer> list = new ArrayList<>();
		customerRepository.findAll().forEach(e -> list.add(e));
		response.setCustomerList(list);
        return response;
    }

	@CrossOrigin(origins = "*")
    @PostMapping(path= "/updateCustomer", consumes = "application/json", produces = "application/json")
	public Customer updateCustomer(@RequestBody Customer customer) {
        //add resource
     	customerRepository.save(customer);
     	User user = userRepository.findById(customer.getUserId()).orElse(null);
     	
     	if (user != null) {
            user.setUser(customer.getName()); // Actualiza el nombre de usuario
            user.setPassword(customer.getPassword()); // Actualiza la contraseña

            // Guarda el usuario actualizado
            userRepository.save(user);
        }
		return customer;
	}
	

	@CrossOrigin(origins = "*")
	@PostMapping(path = "/customerremove", consumes = "application/json")
	public @ResponseBody ResponseEntity<String> deleteCustomerApi(@RequestBody Customer customer) {
		customerRepository.deleteById(customer.getId());
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	@GetMapping(path = "getAllCustomers", produces= "application/json")
	public Customers getCustomers() {
		Customers response = new Customers();
		ArrayList<Customer> list = new ArrayList<>();
		customerRepository.findAll().forEach(c -> list.add(c));
		response.setCustomerList(list);
		return response;				
	}
	

    @PostMapping(path= "/addcustomer", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addCustomer(@RequestBody Customer customer) {       
        //add resource
    	customer = customerRepository.save(customer);
        //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                    .path("/{id}")
                                    .buildAndExpand(customer.getId())
                                    .toUri();
        //Send location in response
        return ResponseEntity.created(location).build();
    }


}
	