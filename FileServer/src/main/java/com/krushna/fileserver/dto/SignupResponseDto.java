package com.krushna.fileserver.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {

    private Long userId;

    private String username;
}
