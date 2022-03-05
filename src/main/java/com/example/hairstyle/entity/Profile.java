package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Profile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String firstname;

    @Column(length = 50)
    private String lastname;

    private Date birthday;

    private Boolean gender;

    @Column(length = 15)
    private String mobile;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String address;

    private String linkAvatar;

    @OneToOne(targetEntity = Account.class, mappedBy = "profile", fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(targetEntity = HairstyleReview.class, mappedBy = "profile")
    @JsonIgnore
    private Set<HairstyleReview> hairstyleReviews = new HashSet<>();

    @OneToMany(targetEntity = Rating.class, mappedBy = "profile")
    @JsonIgnore
    private Set<Rating> ratings = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    @ManyToMany(targetEntity = Discount.class)
    @JsonIgnore
    private Set<Discount> discounts = new HashSet<>();

    @OneToMany(targetEntity = Transaction.class, mappedBy = "profile")
    @JsonBackReference
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(targetEntity = Ordering.class, mappedBy = "profile")
    @JsonBackReference
    private Set<Ordering> orders = new HashSet<>();

    @OneToMany(targetEntity = Behavior.class, mappedBy = "profile")
    @JsonBackReference
    private Set<Behavior> behaviors = new HashSet<>();

    public void addDiscount(Discount discount) {
        this.discounts.add(discount);
        discount.getProfiles().add(this);
    }

    public void removeDiscount(Discount discount) {
        this.discounts.remove(discount);
        discount.getProfiles().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        Profile profile = (Profile) o;
        return Objects.equals(this.id, profile.id)
                && Objects.equals(this.firstname, profile.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.firstname, this.lastname);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + this.id + ", firstname='" + this.firstname + ", lastname='" + this.lastname + '}';
    }
}
