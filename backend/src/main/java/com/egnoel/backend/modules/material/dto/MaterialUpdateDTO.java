package com.egnoel.backend.modules.material.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MaterialUpdateDTO {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    private MultipartFile file;
}
