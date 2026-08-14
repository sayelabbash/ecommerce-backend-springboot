package com.sayel.E_Commerce.controller;

import com.sayel.E_Commerce.dto.ForgotPasswordRequest;
import com.sayel.E_Commerce.dto.LoginRequest;
import com.sayel.E_Commerce.dto.RegisterRequest;
import com.sayel.E_Commerce.dto.ResetPasswordRequest;
import com.sayel.E_Commerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return "Registration successful. Please check your email to verify your account.";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        userService.verifyUser(token);
        return "Account verified successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return "If that email exists, a password reset link has been sent.";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return "Password reset successfully";
    }
}
