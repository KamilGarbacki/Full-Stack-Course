package com.kgarbacki.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDataAccessService customerDataAccessService;
    public CustomerService(CustomerDataAccessService customerDataAccessService) {
        this.customerDataAccessService = customerDataAccessService;
    }

    public List<Customer> getAllCustomers(){
        return customerDataAccessService.selectAllCustomers();
    }

    public Customer getCustomer(Integer id){
        return customerDataAccessService.selectCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id: %s not found".formatted(id)));
    }
}
