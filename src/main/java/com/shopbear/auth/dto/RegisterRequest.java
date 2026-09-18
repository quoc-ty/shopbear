package com.shopbear.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank//không được null, rỗng hoặc toàn khoảng trắng.
    @Email //phải có format email hợp lệ.
    private String email;

    @NotBlank
    @Size(min = 8, max = 100) //password dài 8–100 ký tự.
    private String password;

    @NotBlank
    @Size(max = 100) //giới hạn độ dài fullName dài 100 ký tự.
    private String fullName;
}