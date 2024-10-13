package com.kgarbacki.customer;

import com.kgarbacki.exception.DuplicateResourceException;
import com.kgarbacki.exception.RequestValidationException;
import com.kgarbacki.exception.ResourceNotFoundException;
import com.kgarbacki.s3.S3Buckets;
import com.kgarbacki.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper, S3Service s3Service, S3Buckets s3Buckets) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    public List<CustomerDTO> getAllCustomers(){

        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long customerId){
        return customerDao.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(
                        () ->  new ResourceNotFoundException("customer with id [%s] not found".formatted(customerId))
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException(
                    "Email already taken"
            );
        }

        Customer newCustomer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        );

        customerDao.insertCustomer(newCustomer);
    }

    public void deleteCustomerById(Long customerId) {
        checkIfCustomerExistsOrThrow(customerId);

        customerDao.deleteCustomerById(customerId);
    }

    private void checkIfCustomerExistsOrThrow(Long customerId) {
        if (!customerDao.existsCustomerWithId(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }
    }

    public void updateCustomer(Long customerId,
                               CustomerUpdateRequest request) {

        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        boolean changes = false;

        if(request.name() != null && !request.name().equals(customer.getName())){
            customer.setName(request.name());
            changes = true;
        }
        if(request.email() != null && !request.email().equals(customer.getEmail())){
            if(customerDao.existsCustomerWithEmail(request.email())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }

            customer.setEmail(request.email());
            changes = true;
        }
        if(request.age() != null && !request.age().equals(customer.getAge())){
            customer.setAge(request.age());
            changes = true;
        }
        if(request.gender() != null && !request.gender().equals(customer.getGender())){
            customer.setGender(request.gender());
            changes = true;
        }

        if(!changes){
            throw new RequestValidationException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }

    public void uploadCustomerProfileImage(Long customerId,
                                           MultipartFile file) {
        checkIfCustomerExistsOrThrow(customerId);
        String profileImageId = UUID.randomUUID().toString();
        try {

            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),
                    file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image", e);
        }

        customerDao.updateCustomerProfileImageId(customerId, profileImageId);
    }

    public byte[] getCustomerProfileImage(Long customerId) {
        var customer = customerDao.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        if(StringUtils.isBlank(customer.profileImageId())){
            throw new ResourceNotFoundException(
                    "customer with id [%s] profile image not found".formatted(customerId)
            );
        }

       return s3Service.getObject(
                s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, customer.profileImageId())
        );
    }
}
