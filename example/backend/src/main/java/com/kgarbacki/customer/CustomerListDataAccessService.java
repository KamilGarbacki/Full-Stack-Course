package com.kgarbacki.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {
    private static List<Customer> customers;

    static{
        customers = new ArrayList<>();
        Customer alex = new Customer(1L, "Alex", "alex@gmail.com", 21);
        Customer jamila = new Customer(2L, "Jamila", "jamila@gmail.com", 19);
        customers.add(alex);
        customers.add(jamila);
    }


    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
        return customers.stream().filter(c -> c.getId().equals(customerId)).findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer update) {
        customers.add(update);
    }


}
