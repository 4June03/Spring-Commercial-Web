package com.example.Ecommercial_project.Shoping.website.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 500)
    private String title;
    @Column(length = 5000)
    private String description;

    private String category;

    private Double price;

    private int stock;

    private String image;
}
