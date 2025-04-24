package com.bridgelabz.employeepayroll.dto;

import lombok.Data;

@Data
public class VerificationDTO {
    private String email;
    private String otp;
}
