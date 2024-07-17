package com.kgarbacki.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age,
        Gender gender
) {}
