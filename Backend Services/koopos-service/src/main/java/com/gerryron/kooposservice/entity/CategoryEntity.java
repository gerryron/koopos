package com.gerryron.kooposservice.entity;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<ProductCategories> productCategories;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Set<ProductCategories> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(Set<ProductCategories> productCategories) {
        this.productCategories = productCategories;
    }
}
