package com.bridgelabz.employeepayroll.service;

import com.bridgelabz.employeepayroll.dto.LoginDTO;
import com.bridgelabz.employeepayroll.dto.RegisterDTO;
import com.bridgelabz.employeepayroll.dto.ResetPasswordDTO;
import com.bridgelabz.employeepayroll.dto.AccountVerificationDTO;
import com.bridgelabz.employeepayroll.exceptionhandlers.CustomException;
import com.bridgelabz.employeepayroll.model.User;
import com.bridgelabz.employeepayroll.repository.UserRepository;
import com.bridgelabz.employeepayroll.utility.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements IUserService{
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
    public void verifyUser(AccountVerificationDTO accountVerificationDTO){
        Optional<User> userOptional = userRepository.findByEmail(accountVerificationDTO.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getOtp().equals(accountVerificationDTO.getOtp())) {
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

    public void forgetPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new CustomException("No user found with email: " + email);
        }

        User user = userOptional.get();
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setOtp(otp);
        userRepository.save(user);

        String message = "Dear " + user.getFullName()
                + "\nYour OTP for password reset is: " + otp
                + "\nPlease use this OTP to set your new password.";
        emailService.sendEmail(user.getEmail(), "Password Reset OTP", message);
    }

    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Optional<User> userOptional = userRepository.findByEmail(resetPasswordDTO.getEmail());
        if (userOptional.isEmpty()) {
            throw new CustomException("No user found with email: " + resetPasswordDTO.getEmail());
        }

        User user = userOptional.get();
        if (!resetPasswordDTO.getOtp().equals(user.getOtp())) {
            throw new CustomException("Invalid OTP provided.");
        }

        user.setPassword(encoder.encode(resetPasswordDTO.getNewPassword()));
        user.setOtp(null);
        userRepository.save(user);
    }
}
