package com.gerryron.koopos.grocerystoreservice.entity;


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

    @ManyToMany(mappedBy = "categories")
    private Set<InventoryEntity> inventories;

    public CategoryEntity(String categoryName) {
        this.name = categoryName;
    }

    @PrePersist
    void preInsert() {
        this.createdDate = LocalDateTime.now();
    }
}
