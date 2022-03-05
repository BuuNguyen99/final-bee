package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Discount extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Column(length = 50)
    private String name;

    @Column(length = 100, unique = true)
    private String slug;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    private Integer currentUse;

    private Integer maxUse;

    private Integer discountAmount;

    private Instant startsAt;

    private Instant expiresAt;

    @ManyToMany(targetEntity = Profile.class, mappedBy = "discounts")
    @JsonIgnore
    private Set<Profile> profiles = new HashSet<>();

    @OneToMany(targetEntity = Ordering.class, mappedBy = "promo")
    @JsonBackReference
    private Set<Ordering> orders = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }
        var discount = (Discount) o;

        return Objects.equals(id, discount.id)
                && Objects.equals(code, discount.code)
                && Objects.equals(name, discount.name)
                && Objects.equals(currentUse, discount.currentUse)
                && Objects.equals(maxUse, discount.maxUse)
                && Objects.equals(discountAmount, discount.discountAmount)
                && Objects.equals(startsAt, discount.startsAt)
                && Objects.equals(expiresAt, discount.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, currentUse, maxUse, discountAmount, startsAt, expiresAt);
    }
}
