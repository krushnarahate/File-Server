package com.krushna.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class LoginResponseDto {

    private String token;

    private Long userId;

    private String role;
}
