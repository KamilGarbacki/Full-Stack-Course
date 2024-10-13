package com.kgarbacki.customer;

import com.kgarbacki.exception.DuplicateResourceException;
import com.kgarbacki.exception.RequestValidationException;
import com.kgarbacki.exception.ResourceNotFoundException;
import com.kgarbacki.s3.S3Buckets;
import com.kgarbacki.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, passwordEncoder, customerDTOMapper, s3Service, s3Buckets);

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
                id, "kamil", "kamil@gmail.com", "password", 21,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        CustomerDTO actual = underTest.getCustomerById(id);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerByIdReturnsEmptyOptional() {
        Long id = 10L;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        String email = "kamil@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "kamil", email, "password", 21, Gender.MALE
        );
        String passwordHash = "fqgfeim32ingt4oiknt2pi3n5in2mt";

        when(passwordEncoder.encode("password")).thenReturn(passwordHash);

        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getId()).isNull();
        assertThat(captureCustomer.getName()).isEqualTo(request.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(request.email());
        assertThat(captureCustomer.getPassword()).isEqualTo(passwordHash);
        assertThat(captureCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        String email = "kamil@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "kamil", email, "password", 21, Gender.MALE
        );
        assertThatThrownBy(() -> underTest.addCustomer(request))
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

        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        Long id = 10L;
        Customer customer = new Customer(
                id, "Kamil", "kamil@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "kamil@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "KamilG", newEmail, 21, Gender.MALE);

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
                id, "kamil", "kamil@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "KamilG", null, null, null
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
                id, "kamil", "kamil@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "kamil@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null, null
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
                id, "kamil", "kamil@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 21, null
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
                id, "kamil", "kamil@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "kamil@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null, null
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
                id, "Kamil", "kamil@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge(), customer.getGender()
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
                null, null, null, null
        );

        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void canUploadCustomerImage() {
        Long id = 10L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();

        MultipartFile multipartFile = new MockMultipartFile(
                "file", bytes
        );

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        underTest.uploadCustomerProfileImage(
                id, multipartFile
        );

        ArgumentCaptor<String> profileImageIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(customerDao).updateCustomerProfileImageId(
                eq(id),
                profileImageIdArgumentCaptor.capture()
        );

        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(id, profileImageIdArgumentCaptor.getValue()),
                bytes
        );
    }

    @Test
    void canNotUploadCustomerImageWhenCustomerDoesNotExist() {
        Long id = 10L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(id, mock(MultipartFile.class)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        verify(customerDao).existsCustomerWithId(id);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);

    }

    @Test
    void canNotUploadCustomerImageWhenExceptionIsThrown() throws IOException {
        Long id = 10L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);


        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(id, multipartFile))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(IOException.class)
                .hasMessageContaining("Failed to upload profile image");

        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());

    }

    @Test
    void canGetCustomerProfileImage() {
        Long id = 10L;

        String profileImageId = "22222";
        Customer customer = new Customer(
                id, "Kamil", "kamil@gmail.com", "password", 19, Gender.MALE, profileImageId
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();

        when(s3Service.getObject(bucket, "profile-images/%s/%s".formatted(id, profileImageId))
        ).thenReturn(expectedImage);

        byte[] actualImage = underTest.getCustomerProfileImage(id);

        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void canNotGetCustomerProfileImageWhenNoProfileImageId() {
        Long id = 10L;

        Customer customer = new Customer(
                id, "Kamil", "kamil@gmail.com", "password", 19, Gender.MALE
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> underTest.getCustomerProfileImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] profile image not found".formatted(id)
                );


        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);

    }

    @Test
    void canNotGetCustomerProfileImageWhenCustomerDoesNotExists() {
        Long id = 10L;


        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerProfileImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));


        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);

    }
}