package com.govqa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementUpsertRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private LocalDateTime publishTime;
    private Integer isTop = 0;
    private Integer status = 1;
}
