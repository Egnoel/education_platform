package com.egnoel.backend.modules.auth.dto;

import lombok.Data;

@Data
public class AdminRegisterDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long institutionId;
}
