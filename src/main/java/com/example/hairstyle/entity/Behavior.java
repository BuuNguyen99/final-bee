package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter@Setter
@NoArgsConstructor
public class Behavior extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer time;

    @ManyToOne(targetEntity = Profile.class)
    @JoinColumn(name = "profile_id",nullable = false)
    @JsonManagedReference
    private Profile profile;

    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name = "product_id",nullable = false)
    @JsonManagedReference
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var behavior = (Behavior) o;
        return Objects.equals(id, behavior.id)
                && Objects.equals(time, behavior.time);
    }


    public void addProduct(Product product) {
        product.getBehaviorItems().add(this);
        this.setProduct(product);
    }

    public void removeProduct(Product product) {
        product.getBehaviorItems().remove(this);
        this.setProduct(null);
    }

    public void addProfile(Profile profile) {
        profile.getBehaviors().add(this);
        this.setProduct(product);
    }

    public void removeProfile(Profile profile) {
        profile.getBehaviors().remove(this);
        this.setProfile(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time);
    }
}
