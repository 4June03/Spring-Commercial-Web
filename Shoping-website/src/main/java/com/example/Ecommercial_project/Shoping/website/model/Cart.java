package com.example.Ecommercial_project.Shoping.website.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private UserDtls user;
    @ManyToOne
    private Product product;
    private Integer quantity;
    @Transient //sử dụng để đánh dấu một thuộc tính của entity không nên được lưu trữ vào database
    private  Double totalPrice;

    @Transient
    private Double totalOrderPrice;


}
