package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor

@Table(name = "user_product_list")
@Builder
public class UserProductList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> productIds;

    public UserProductList() {
    }

    public UserProductList(User user) {
        this.user = user;
        this.productIds = new ArrayList<String>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
}
