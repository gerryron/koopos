package com.gerryron.kooposservice.dto.request;

import javax.validation.constraints.NotBlank;

public class CategoryRequest {

    @NotBlank
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
