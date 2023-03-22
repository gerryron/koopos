package com.gerryron.koopos.grocerystoreservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@NoArgsConstructor
public class Category {
    @NotBlank
    private String name;
    @JsonIgnore
    private Set<Item> items;

    public Category(String name) {
        this.name = name;
    }
}
