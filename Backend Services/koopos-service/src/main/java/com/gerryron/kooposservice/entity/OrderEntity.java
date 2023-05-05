package com.gerryron.kooposservice.entity;

import com.gerryron.kooposservice.enums.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String orderNumber;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetailsEntity> orderDetailsEntities;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    public Set<OrderDetailsEntity> getOrderDetailsEntities() {
        return orderDetailsEntities;
    }

    public void setOrderDetailsEntities(Set<OrderDetailsEntity> orderDetailsEntities) {
        this.orderDetailsEntities = orderDetailsEntities;
    }

}
