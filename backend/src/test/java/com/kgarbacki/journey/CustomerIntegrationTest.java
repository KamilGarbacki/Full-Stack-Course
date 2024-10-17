package com.kgarbacki.journey;

import com.github.javafaker.Faker;
import com.kgarbacki.AbstractTestcontainer;
import com.kgarbacki.customer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.shaded.com.google.common.io.Files;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest extends AbstractTestcontainer {

    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker();
    private static final String CUSTOMER_PATH = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {
        // create registration request
        String name = FAKER.name().firstName();
        String email = name + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(16, 100) ;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
            name, email, "password", age, Gender.MALE
        );
        //send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        Long id = allCustomers
                .stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                request.name(),
                request.email(),
                request.gender(),
                request.age(),
                List.of("ROLE_USER"),
                request.email(),
                null
        );

        //make sure that customer is present
        assertThat(allCustomers).contains(expectedCustomer);

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        String name = FAKER.name().firstName();
        String email = name + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(16, 100) ;

        CustomerRegistrationRequest request1 = new CustomerRegistrationRequest(
                name, email, "password", age, Gender.MALE
        );

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name, email + ".pl", "password", age, Gender.MALE
        );

        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request1), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);


        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();


        Long id = allCustomers
                .stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        String name = FAKER.name().firstName();
        String email = name + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(16, 100) ;

        CustomerRegistrationRequest request1 = new CustomerRegistrationRequest(
                name, email, "password", age, Gender.MALE
        );

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name, email + ".pl", "password", age, Gender.MALE
        );

        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request1), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);


        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        Long id = allCustomers
                .stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "kamil", "kamil05@gmail.com", 21, Gender.MALE
        );

        webTestClient.patch()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                updateRequest.name(),
                updateRequest.email(),
                updateRequest.gender(),
                updateRequest.age(),
                List.of("ROLE_USER"),
                updateRequest.email(),
                null
        );

        webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canUploadAndDownloadProfilePicture() throws IOException {
        String name = FAKER.name().firstName();
        String email = name + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(16, 100) ;

        CustomerRegistrationRequest request1 = new CustomerRegistrationRequest(
                name, email, "password", age, Gender.MALE
        );

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name, email + ".pl", "password", age, Gender.MALE
        );

        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request1), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);


        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        CustomerDTO customerDTO = allCustomers
                .stream()
                .filter(customer -> customer.email().equals(email))
                .findFirst()
                .orElseThrow();

        assertThat(customerDTO.profileImageId()).isNullOrEmpty();

        Resource image = new ClassPathResource(
                "%s.jpg".formatted(customerDTO.gender().name().toLowerCase())
        );

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", image);

         webTestClient.post()
                 .uri(CUSTOMER_PATH + "/{customerId}/profile-image", customerDTO.id())
                 .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                 .header(AUTHORIZATION, "Bearer " + jwtToken)
                 .exchange()
                 .expectStatus()
                 .isOk();

         String profileImageId = webTestClient.get()
                 .uri(CUSTOMER_PATH + "/{id}", customerDTO.id())
                 .accept(MediaType.APPLICATION_JSON)
                 .header(AUTHORIZATION, "Bearer " + jwtToken)
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(CustomerDTO.class)
                 .returnResult()
                 .getResponseBody()
                 .profileImageId();

         assertThat(profileImageId).isNotNull().isNotBlank();

        byte[] downloadedImage = webTestClient.get()
                .uri(CUSTOMER_PATH + "/{customerId}/profile-image", customerDTO.id())
                .accept(MediaType.IMAGE_JPEG)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();

        byte[] actual = Files.toByteArray(image.getFile());

        assertThat(actual).isEqualTo(downloadedImage);
    }
}
