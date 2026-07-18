package com.krushna.fileserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedFileResponseDto {

    private Long id;

    private String fileName;

    private String owner;

}
