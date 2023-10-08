package com.example.demo.category.entity;

import com.example.demo.item.entity.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;


}
