package com.example.Ecommercial_project.Shoping.website.repository;

import com.example.Ecommercial_project.Shoping.website.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    public Cart findByUserIdAndProductId(Integer userId, Integer productId);
}
