package com.govqa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryUpsertRequest {
    @NotBlank
    private String name;
    private Integer sortOrder = 0;
    private Integer status = 1;
}
