package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class HairstyleReview extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double rating;

    private Boolean published;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @ManyToOne
    @JoinColumn(name="profile_id",nullable = false)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name="hairstyle_id",nullable = false)
    @JsonIgnore
    private Hairstyle hairstyle;

    public void addProfile(Profile profile) {
        this.profile = profile;
        profile.getHairstyleReviews().add(this);
    }

    public void removeProfile(Profile profile) {
        this.profile = null;
        profile.getHairstyleReviews().remove(this);
    }

    public void addHairstyle(Hairstyle hairstyle) {
        this.hairstyle = hairstyle;
        hairstyle.getHairstyleReviews().add(this);
    }

    public void removeHairstyle(Hairstyle hairstyle) {
        this.hairstyle = null;
        hairstyle.getHairstyleReviews().remove(this);
    }
}
