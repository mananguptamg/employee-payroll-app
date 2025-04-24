package com.bridgelabz.employeepayroll.controller;

import com.bridgelabz.employeepayroll.dto.LoginDTO;
import com.bridgelabz.employeepayroll.dto.RegisterDTO;
import com.bridgelabz.employeepayroll.dto.ResetPasswordDTO;
import com.bridgelabz.employeepayroll.dto.AccountVerificationDTO;
import com.bridgelabz.employeepayroll.service.UserService;
import com.bridgelabz.employeepayroll.utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employeepayrollservice/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO registerDTO) {
        userService.registerUser(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody AccountVerificationDTO accountVerificationDTO){
        userService.verifyUser(accountVerificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User verified successfully.");
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.loginUser(loginDTO);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/forget/{email}")
    public ResponseEntity<String> forgetPassword(@PathVariable String email){
        userService.forgetPassword(email);
        return ResponseEntity.status(HttpStatus.CREATED).body("Password reset otp successfully sent.");
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Password reset successfully.");
    }
}