package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Ordering extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String sessionId;

    @Column(length = 100)
    private String token;

    private Double subTotal; //total price of order items

    private Double discount; //total discount of order items

    private Double tax;

    private Double shipping;

    private Double total; // includes tax + shipping, excludes discount

    private Double grandTotal;

    @Column(length = 50)
    private String firstname;

    @Column(length = 50)
    private String middleName;

    @Column(length = 50)
    private String lastname;

    @Column(length = 15)
    private String mobile;

    @Column(length = 50)
    private String email;

    private String address;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @OneToMany(targetEntity = Transaction.class, mappedBy = "order")
    @JsonBackReference
    private Set<Transaction> transactions;

    @OneToMany(targetEntity = OrderItem.class, mappedBy = "order")
    @JsonBackReference
    private Set<OrderItem> orderItems;

    @ManyToOne(targetEntity = Discount.class)
    @JoinColumn(name = "promo_id")
    @JsonManagedReference
    private Discount promo;

    @ManyToOne(targetEntity = OrderStatus.class)
    @JoinColumn(name = "status_id", nullable = false)
    @JsonManagedReference
    private OrderStatus status;

    @ManyToOne(targetEntity = Profile.class)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonManagedReference
    private Profile profile;
}
