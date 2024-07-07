package com.kgarbacki;

import com.github.javafaker.Faker;
import com.kgarbacki.customer.Customer;
import com.kgarbacki.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
        //...
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
//            Customer alex = new Customer("Alex", "alex1@gmail.com", 21);
//            Customer jamila = new Customer("Jamila", "jamila@gmail.com", 19);
//
//            List<Customer> customers = List.of(alex, jamila);
//            customerRepository.saveAll(customers);
            // new comment

            Faker faker = new Faker();
            Random random = new Random();

            String name = faker.name().firstName();
            Integer age = random.nextInt(16, 99);
            String email = name.toLowerCase() + age + "@gmail.com";

            Customer customer = new Customer(name, email, age);
            customerRepository.save(customer);
        };
    }
}
