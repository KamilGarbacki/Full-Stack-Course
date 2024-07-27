package com.kgarbacki.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest{

    private CustomerJPADataAccessService underTest;
    AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable =  MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();

        verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {
        Long id = 1L;

        underTest.selectCustomerById(id);

        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                "kamil",
                "kamil@gmail.com",
                "password", 20,
                Gender.MALE);

        underTest.insertCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        String email = "email@gmail.com";

        underTest.existsCustomerWithEmail(email);

        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existsCustomerWithId() {
        Long id = 1L;

        underTest.existsCustomerWithId(id);

        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomerById() {
        Long id = 1L;

        underTest.deleteCustomerById(id);

        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                "kamil",
                "kamil@gmail.com",
                "password", 20,
                Gender.MALE);

        underTest.updateCustomer(customer);

        verify(customerRepository).save(customer);
    }
}