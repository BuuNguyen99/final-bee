package com.example.hairstyle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private Boolean enabled;

    @Column(length = 50, unique = true)
    private String email;

    @JsonIgnore
    private String verificationCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore
    private Profile profile;

    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account)) {
            return false;
        }
        var account = (Account) o;
        return Objects.equals(this.id, account.id)
                && Objects.equals(this.username, account.username)
                && Objects.equals(this.roles, account.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username, this.roles);
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + this.id + ", name='" + this.username + '\'' + ", role='" + this.roles + '\'' + '}';
    }
}
