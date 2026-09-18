package com.shopbear.auth.dto;

import com.shopbear.customer.dto.CustomerResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {

    private CustomerResponse customer;

    private String accessToken;

    private String refreshToken;
}