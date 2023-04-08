package com.gerryron.koopos.grocerystoreservice.entity;


import com.gerryron.koopos.grocerystoreservice.shared.dto.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "name")
})
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER)
    private Set<ProductEntity> productEntities;

    public CategoryEntity(String categoryName) {
        this.name = categoryName;
    }

    public CategoryEntity(Category category) {
        this.name = category.getCategoryName();
    }

    @PrePersist
    void preInsert() {
        this.createdDate = LocalDateTime.now();
    }
}
