package com.gerryron.koopos.grocerystoreservice.entity;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
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
@Table(name = "inventory")
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String barcode;
    private String itemName;
    @Column(columnDefinition = "text")
    private String description;
    private Integer quantity;
    private BigDecimal buyingPrice;
    private BigDecimal sellingPrice;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "inventory_categories",
            joinColumns = @JoinColumn(name = "inventory_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<CategoryEntity> categories;

    public InventoryEntity(Item item) {
        this.barcode = item.getBarcode();
        this.itemName = item.getItemName();
        this.description = item.getDescription();
        this.quantity = item.getQuantity();
        this.buyingPrice = item.getBuyingPrice();
        this.sellingPrice = item.getSellingPrice();
        this.updatedDate = LocalDateTime.now();
        if (null != item.getCategories()) {
            this.categories = item.getCategories().stream()
                    .map(CategoryEntity::new)
                    .collect(Collectors.toSet());
        }
    }

    @PrePersist
    void preInsert() {
        this.createdDate = LocalDateTime.now();
    }
}
