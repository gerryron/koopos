package com.gerryron.koopos.grocerystoreservice.entity;

import com.gerryron.koopos.grocerystoreservice.shared.request.ProductDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String barcode;
    private String productName;
    @Column(columnDefinition = "text")
    private String description;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal sellingPrice;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<CategoryEntity> categories;

    public ProductEntity(ProductDto productDto) {
        this.barcode = productDto.getBarcode();
        this.productName = productDto.getProductName();
        this.description = productDto.getDescription();
        this.quantity = productDto.getQuantity();
        this.buyingPrice = productDto.getBuyingPrice();
        this.sellingPrice = productDto.getSellingPrice();
        this.updatedDate = LocalDateTime.now();
        if (null != productDto.getCategories()) {
            this.categories = productDto.getCategories().stream()
                    .map(CategoryEntity::new)
                    .collect(Collectors.toSet());
        }
    }

    @PrePersist
    void preInsert() {
        this.createdDate = LocalDateTime.now();
    }
}
