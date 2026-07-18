package com.krushna.fileserver.controller;

import com.krushna.fileserver.dto.LoginRequestDto;
import com.krushna.fileserver.dto.LoginResponseDto;
import com.krushna.fileserver.dto.SignupRequestDto;
import com.krushna.fileserver.dto.SignupResponseDto;
import com.krushna.fileserver.entity.User;
import com.krushna.fileserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public SignupResponseDto saveUser(@RequestBody SignupRequestDto user) {
        return userService.signup(user);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {
        return userService.login(request);
    }


}
