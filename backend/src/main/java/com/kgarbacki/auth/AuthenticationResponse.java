package com.kgarbacki.auth;


import com.kgarbacki.customer.CustomerDTO;

public record AuthenticationResponse(String jwtToken,
                                     CustomerDTO customerDTO) {}
