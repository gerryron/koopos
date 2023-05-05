package com.gerryron.kooposservice.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "products_categories")
public class ProductCategoryEntity {

    @EmbeddedId
    private CompositeKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public CompositeKey getId() {
        return id;
    }

    public void setId(CompositeKey id) {
        this.id = id;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    @Embeddable
    public static class CompositeKey implements Serializable {
        @Column(name = "product_id")
        private Integer productId;
        @Column(name = "category_id")
        private Integer categoryId;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CompositeKey that = (CompositeKey) o;

            if (!productId.equals(that.productId)) return false;
            return categoryId.equals(that.categoryId);
        }

        @Override
        public int hashCode() {
            int result = productId.hashCode();
            result = 31 * result + categoryId.hashCode();
            return result;
        }
    }
}
