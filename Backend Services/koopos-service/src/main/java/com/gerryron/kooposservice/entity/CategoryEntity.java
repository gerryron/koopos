package com.gerryron.kooposservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
