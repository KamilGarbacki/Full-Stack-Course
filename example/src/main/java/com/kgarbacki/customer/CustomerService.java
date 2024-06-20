package com.kgarbacki.customer;

import com.kgarbacki.exception.DuplicateResourceExeption;
import com.kgarbacki.exception.RequestValidationException;
import com.kgarbacki.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Long customerId){
        return customerDao.selectCustomerById(customerId)
                .orElseThrow(() ->  new ResourceNotFoundException("customer with id [%s] not found".formatted(customerId)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsPersonWithEmail(email)) {
            throw new DuplicateResourceExeption(
                    "Email already taken"
            );
        }

        Customer newCustomer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDao.insertCustomer(newCustomer);
    }

    public void deleteCustomerById(Long customerId) {
        if (!customerDao.existsPersonWithId(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }

        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Long customerId,
                               CustomerUpdateRequest request) {
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if(request.name() != null && !request.name().equals(customer.getName())){
            customer.setName(request.name());
            changes = true;
        }
        if(request.email() != null && !request.email().equals(customer.getEmail())){
            customer.setEmail(request.email());
            changes = true;
        }
        if(request.age() != null && !request.age().equals(customer.getAge())){
            customer.setAge(request.age());
            changes = true;
        }

        if(!changes){
            throw new RequestValidationException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }
}
