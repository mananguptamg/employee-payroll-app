package com.bridgelabz.employeepayroll.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String fullName;
    private String email;
    private String password;
    private String otp;
}
