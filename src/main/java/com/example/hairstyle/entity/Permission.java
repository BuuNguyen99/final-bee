package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String codename;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonManagedReference
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }
        Permission permission = (Permission) o;
        return Objects.equals(this.id, permission.id)
                && Objects.equals(this.name, permission.name)
                && Objects.equals(this.codename, permission.codename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.codename);
    }

    @Override
    public String toString() {
        return "Role{" + "id=" + this.id + ", name='" + this.name + ", codename='" + this.codename + '}';
    }
}