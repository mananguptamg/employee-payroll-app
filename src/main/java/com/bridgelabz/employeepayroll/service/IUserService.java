package com.bridgelabz.employeepayroll.service;

import com.bridgelabz.employeepayroll.dto.LoginDTO;
import com.bridgelabz.employeepayroll.dto.RegisterDTO;
import com.bridgelabz.employeepayroll.dto.ResetPasswordDTO;
import com.bridgelabz.employeepayroll.dto.AccountVerificationDTO;

public interface IUserService {
    void registerUser(RegisterDTO registerDTO);
    void verifyUser(AccountVerificationDTO accountVerificationDTO);
    String loginUser(LoginDTO loginDTO);
    void forgetPassword(String email);
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}
