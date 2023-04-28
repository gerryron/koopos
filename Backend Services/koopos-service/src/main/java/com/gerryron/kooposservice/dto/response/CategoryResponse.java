package com.gerryron.kooposservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Set;

public class CategoryResponse {

    private String categoryName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;
    private Set<Product> products;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public static class Product {
        private final String barcode;
        private final String productName;

        public Product(Builder builder) {
            this.barcode = builder.barcode;
            this.productName = builder.productName;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getBarcode() {
            return barcode;
        }

        public String getProductName() {
            return productName;
        }

        public static class Builder {
            private String barcode;
            private String productName;

            public Builder barcode(String barcode) {
                this.barcode = barcode;
                return this;
            }

            public Builder productName(String productName) {
                this.productName = productName;
                return this;
            }

            public Product build() {
                return new Product(this);
            }
        }
    }
}
