package com.kgarbacki.customer;

import com.kgarbacki.exception.DuplicateResourceException;
import com.kgarbacki.exception.RequestValidationException;
import com.kgarbacki.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();

        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "kamil", "kamil@gmail.com", 21
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Customer actual = underTest.getCustomerById(id);

        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerByIdReturnsEmptyOptional() {
        Long id = 10L;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = "kamil@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "kamil", email, 21
        );
        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getId()).isNull();
        assertThat(captureCustomer.getName()).isEqualTo(request.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(request.email());
        assertThat(captureCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        String email = "kamil@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "kamil", email, 21
        );
        assertThatThrownBy(()-> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        Long id = 1L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        underTest.deleteCustomerById(id);

        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenCustomerDoesntExistWhileDeletingCustomerById() {
        Long id = 1L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        assertThatThrownBy(()-> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "Kamil", "kamil@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "kamil@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "KamilG", newEmail, 21);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "kamil", "kamil@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "KamilG", null, null
        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "kamil", "kamil@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "kamil@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "kamil", "kamil@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 21
        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenEmailAlreadyTaken() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "kamil", "kamil@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "kamil@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null
        );

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "Kamil", "kamil@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerForUpdateDoesntExist() {
        Long id = 10L;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, null
        );

        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).updateCustomer(any());
    }
}