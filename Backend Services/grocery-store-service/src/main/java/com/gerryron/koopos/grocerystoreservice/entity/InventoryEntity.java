package com.gerryron.koopos.grocerystoreservice.entity;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "inventory")
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public InventoryEntity(Item item) {
        this.barcode = item.getBarcode();
        this.itemName = item.getItemName();
        this.description = item.getDescription();
        this.quantity = item.getQuantity();
        this.buyingPrice = item.getBuyingPrice();
        this.sellingPrice = item.getSellingPrice();
    }

    @PrePersist
    void preInsert() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
}
