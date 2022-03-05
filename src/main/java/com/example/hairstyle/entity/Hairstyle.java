package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Hairstyle extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String slug;

    private String hairCut;

    private Boolean gender;

    private String style;

    private String hairLength;

    private Double averageRating;

    private String imageUrl;

    @ManyToMany(targetEntity = FaceShape.class)
    private Set<FaceShape> faceShapes = new HashSet<>();

    @ManyToMany(targetEntity = AgeRange.class)
    private Set<AgeRange> ageRanges = new HashSet<>();

    @OneToMany(targetEntity = HairstyleReview.class, mappedBy = "hairstyle")
    private Set<HairstyleReview> hairstyleReviews = new HashSet<>();
}