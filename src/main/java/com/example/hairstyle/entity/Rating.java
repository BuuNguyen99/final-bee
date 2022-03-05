package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rating extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double rating;

    private Boolean published;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private Product product;

    public void addProfile(Profile profile) {
        this.profile = profile;
        profile.getRatings().add(this);
    }

    public void removeProfile(Profile profile) {
        this.profile = null;
        profile.getRatings().remove(this);
    }

    public void addProduct(Product product) {
        this.product = product;
        product.getRatings().add(this);
    }

    public void removeProduct(Product product) {
        this.product = null;
        product.getRatings().remove(this);
    }
}
