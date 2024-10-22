package com.example.Ecommercial_project.Shoping.website.service;

import com.example.Ecommercial_project.Shoping.website.model.OrderRequest;
import com.example.Ecommercial_project.Shoping.website.model.ProductOrder;

public interface OrderService {

    public ProductOrder saveOrder(Integer userId, OrderRequest orderRequest);

}
