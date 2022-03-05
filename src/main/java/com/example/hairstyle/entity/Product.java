package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String title;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String metaTitle;

    @Column(unique = true)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String slug;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String summary;

    private Double discount;

    private Double price;

    private Integer quantity;

    private Double averageRating;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String category;

    @OneToMany(targetEntity = CartItem.class, mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonBackReference
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToMany(targetEntity = OrderItem.class, mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonBackReference
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(targetEntity = Behavior.class, mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonBackReference
    private Set<Behavior> behaviorItems = new HashSet<>();

    @OneToMany(targetEntity = Rating.class, mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonBackReference
    private Set<Rating> ratings = new HashSet<>();

    @OneToMany(targetEntity = Image.class, mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Image> images = new HashSet<>();

    public void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setProduct(this);
    }

    public void removeCartItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.setProduct(null);
    }

    public void addBehavior(Behavior behavior) {
        this.behaviorItems.add(behavior);
        behavior.setProduct(this);
    }

    public void removeBehavior(Behavior behavior) {
        this.cartItems.remove(behavior);
        behavior.setProduct(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var product = (Product) o;
        return Objects.equals(id, product.id)
                && Objects.equals(title, product.title)
                && Objects.equals(slug, product.slug)
                && Objects.equals(discount, product.discount)
                && Objects.equals(price, product.price)
                && Objects.equals(quantity, product.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, slug, discount, price, quantity);
    }
}
