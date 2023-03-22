package com.gerryron.koopos.grocerystoreservice.entity;


import com.gerryron.koopos.grocerystoreservice.dto.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public CategoryEntity(Category category) {
        this.name = category.getName();
    }

    @PrePersist
    void preInsert() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
}
