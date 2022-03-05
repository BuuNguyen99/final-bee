package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class TransactionType extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    private Integer code;

    @OneToMany(targetEntity = Transaction.class, mappedBy = "type")
    @JsonBackReference
    private Set<Transaction> transactions;
}
