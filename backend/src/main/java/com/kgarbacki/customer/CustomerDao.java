package com.kgarbacki.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDao {

    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Long customerId);

    void insertCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    boolean existsCustomerWithId(Long id);

    void deleteCustomerById(Long customerId);

    void updateCustomer(Customer update);

    Optional<Customer> selectUserByEmail(String email);
}
