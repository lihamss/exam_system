package com.exam.controller;

import com.exam.common.Result;
import com.exam.dto.LoginRequest;
import com.exam.dto.LoginResponse;
import com.exam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse res = authService.login(request);
        return Result.ok(res);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody LoginRequest request) {
        authService.register(request);
        return Result.ok();
    }
}
