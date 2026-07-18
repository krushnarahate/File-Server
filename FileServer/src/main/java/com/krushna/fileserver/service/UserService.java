package com.krushna.fileserver.service;

import com.krushna.fileserver.dto.LoginRequestDto;
import com.krushna.fileserver.dto.LoginResponseDto;
import com.krushna.fileserver.dto.SignupRequestDto;
import com.krushna.fileserver.dto.SignupResponseDto;
import com.krushna.fileserver.entity.Activity;
import com.krushna.fileserver.entity.User;
import com.krushna.fileserver.repository.ActivityRepository;
import com.krushna.fileserver.repository.UserRepository;
import com.krushna.fileserver.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    //register user
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {

        User existingUser = userRepository
                .findByUsername(
                        signupRequestDto.getUsername()
                )
                .orElse(null);

        if(existingUser != null) {
            throw new IllegalStateException(
                    "Username already exists"
            );
        }

        User user = User.builder()
                .username(signupRequestDto.getUsername())
                .email(signupRequestDto.getEmail())
                .password(passwordEncoder.encode(
                        signupRequestDto.getPassword()))
                .role("USER")
                .enabled(true)
                .build();

        user = userRepository.save(user);

        return new SignupResponseDto(
                (long) user.getUserId(),
                user.getUsername()
        );
    }

    //login user
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        Authentication authentication =
                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(
                                loginRequestDto.getUsername(),
                                loginRequestDto.getPassword()
                        )
                );

        User user = (User) authentication.getPrincipal();

        Activity activity = new Activity();

        activity.setUsername(user.getUsername());

        activity.setAction("LOGIN");

        activity.setFileName(null);

        activity.setTime(LocalDateTime.now());

        activityRepository.save(activity);

        String token =
                authUtil.generateAccessToken(user);

        return new LoginResponseDto(
                token,
                (long) user.getUserId(),
                user.getRole()
        );
    }


}
