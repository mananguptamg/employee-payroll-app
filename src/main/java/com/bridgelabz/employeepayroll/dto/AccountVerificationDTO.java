package com.bridgelabz.employeepayroll.dto;

import lombok.Data;

@Data
public class AccountVerificationDTO {
    private String email;
    private String otp;
}
