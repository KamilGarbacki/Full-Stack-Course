package com.kgarbacki.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {}
