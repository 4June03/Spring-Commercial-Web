package com.example.Ecommercial_project.Shoping.website.service;

import com.example.Ecommercial_project.Shoping.website.model.Cart;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId, Integer userId);

    public List<Cart> getCartsByUser(Integer userId);

    public Integer getCountCart(Integer userId);

    public  void updateQuantity(String sy, Integer cid);
}
