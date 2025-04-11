package com.egnoel.backend.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRegisterDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long institutionId;
}
