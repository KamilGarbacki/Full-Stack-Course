package com.kgarbacki.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDao {

    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer customerId);

    void insertCustomer(Customer customer);

    boolean existsPersonWithEmail(String email);

    boolean existsPersonWithId(Integer id);

    void deleteCustomerById(Integer customerId);

    void updateCustomer(Customer update);
}
