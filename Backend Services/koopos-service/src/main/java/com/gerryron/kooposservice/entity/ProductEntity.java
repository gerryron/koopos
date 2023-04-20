package com.gerryron.kooposservice.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(columnNames = "barcode", name = "barcode")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<ProductCategories> productCategories;

    public BigDecimal getProfit() {
        return sellingPrice.subtract(buyingPrice);
    }
}
