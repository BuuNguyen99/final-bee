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
public class AgeRange extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer min;

    private Integer max;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String rangeDescription;

    @ManyToMany(targetEntity = Hairstyle.class,mappedBy = "ageRanges")
    @JsonIgnore
    private Set<Hairstyle> hairstyles;
}
