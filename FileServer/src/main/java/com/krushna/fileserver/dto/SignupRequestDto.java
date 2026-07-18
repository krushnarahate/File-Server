package com.krushna.fileserver.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequestDto {

    private String username;

    private String password;

    private String email;

}
