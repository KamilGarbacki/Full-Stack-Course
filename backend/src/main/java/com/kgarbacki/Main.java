package com.kgarbacki;

import com.github.javafaker.Faker;
import com.kgarbacki.customer.Customer;
import com.kgarbacki.customer.CustomerRepository;
import com.kgarbacki.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
        //...
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder){
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();

            String name = faker.name().firstName();
            Integer age = random.nextInt(16, 99);
            String email = name.toLowerCase() + age + "@gmail.com";
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

            Customer customer = new Customer(name, email, passwordEncoder.encode(UUID.randomUUID().toString()), age, gender);
            customerRepository.save(customer);
        };
    }
}
