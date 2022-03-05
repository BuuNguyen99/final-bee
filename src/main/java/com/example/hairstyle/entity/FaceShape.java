package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FaceShape extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @ManyToMany(targetEntity = Hairstyle.class, mappedBy = "faceShapes")
    @JsonIgnore
    private Set<Hairstyle> hairstyles;
}
