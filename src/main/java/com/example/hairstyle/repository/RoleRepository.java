package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query("select role from Role role where role.id in :ids")
    Set<Role> findRoleByIds(@Param("ids") Set<Long> id);

    @Override
    Page<Role> findAll(Pageable pageable);
}
