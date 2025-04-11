package com.egnoel.backend.modules.material.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MaterialCreateDTO {
    @NotBlank(message = "O título é obrigatório")
    private String title;

    @NotBlank(message = "O caminho do ficheiro é obrigatório")
    private String filePath;

    @NotNull(message = "O ficheiro é obrigatório")
    private MultipartFile file;

    @NotNull(message = "A disciplina é obrigatória")
    private Long subjectId;

    private Long classeId;
}
