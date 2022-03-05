package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Transaction extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String code;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @ManyToOne(targetEntity = TransactionStatus.class)
    @JoinColumn(name = "status_id", nullable = false)
    @JsonManagedReference
    private TransactionStatus status;

    @ManyToOne(targetEntity = TransactionType.class)
    @JoinColumn(name = "type_id", nullable = false)
    @JsonManagedReference
    private TransactionType type;

    @ManyToOne(targetEntity = TransactionMode.class)
    @JoinColumn(name = "mode_id", nullable = false)
    @JsonManagedReference
    private TransactionMode mode;

    @ManyToOne(targetEntity = Profile.class)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonManagedReference
    private Profile profile;

    @ManyToOne(targetEntity = Ordering.class)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonManagedReference
    private Ordering order;
}
