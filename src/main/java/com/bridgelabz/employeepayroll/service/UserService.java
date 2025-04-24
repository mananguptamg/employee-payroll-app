package com.bridgelabz.employeepayroll.service;

import com.bridgelabz.employeepayroll.dto.LoginDTO;
import com.bridgelabz.employeepayroll.dto.RegisterDTO;
import com.bridgelabz.employeepayroll.dto.VerificationDTO;
import com.bridgelabz.employeepayroll.exceptionhandlers.CustomException;
import com.bridgelabz.employeepayroll.model.User;
import com.bridgelabz.employeepayroll.repository.UserRepository;
import com.bridgelabz.employeepayroll.utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtility jwtUtility;

    public void registerUser(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new CustomException("Email is already registered.");
        }

        User user = new User();
        user.setFullName(registerDTO.getFullName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(encoder.encode(registerDTO.getPassword()));
        user.setVerified(false);
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setOtp(otp);
        userRepository.save(user);

        String message = "Dear " + user.getFullName()
                + "\nThank you for registering with Employee Payroll Application"
                + "\nYour OTP for account verification is: " + otp
                + "\nPlease enter this OTP in the app to verify your account.";
        emailService.sendEmail(user.getEmail(), "User registration successful", message);
    }
    public void verifyUser(VerificationDTO verificationDTO){
        Optional<User> userOptional = userRepository.findByEmail(verificationDTO.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getOtp().equals(verificationDTO.getOtp())) {
                user.setVerified(true);
                user.setOtp(null);
                userRepository.save(user);
            }
            else{
                throw new CustomException("User verification failed");
            }
        }
    }
    public String loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new CustomException("Invalid email or user not registered."));

        if (!encoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid password.");
        }
        else if (!user.isVerified()){
            throw new CustomException("User is not verified");
        }
        return jwtUtility.generateToken(user.getEmail());
    }
}
