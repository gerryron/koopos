package com.gerryron.kooposservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryRequest {
    @NotBlank
    private String categoryName;
}
