package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class OrderItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @ManyToOne(targetEntity = Ordering.class)
    @JoinColumn(name = "order_id",nullable = false)
    @JsonManagedReference
    private Ordering order;

    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name = "product_id",nullable = false)
    @JsonManagedReference
    private Product product;
}
