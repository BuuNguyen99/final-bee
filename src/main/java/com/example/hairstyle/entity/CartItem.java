package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class CartItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private Double unitPrice;

    private Double totalPrice;

    private Double discount;

    private Boolean active;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @ManyToOne
    @JoinColumn(name="cart_id")
    @JsonManagedReference
    private Cart cart;

    @ManyToOne
    @JoinColumn(name="product_id")
    @JsonManagedReference
    private Product product;
}
