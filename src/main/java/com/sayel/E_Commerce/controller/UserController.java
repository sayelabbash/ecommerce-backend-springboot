package com.sayel.E_Commerce.controller;

import com.sayel.E_Commerce.dto.UpdateProfileRequest;
import com.sayel.E_Commerce.dto.UserResponse;
import com.sayel.E_Commerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public UserResponse getMyProfile() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@RequestBody @Valid UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }
}
