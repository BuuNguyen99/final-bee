package com.example.hairstyle.repository;

import com.example.hairstyle.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends PagingAndSortingRepository<Profile,Long> {
    Optional<Profile> findByAccount_Username(String username);

    @Query("select profile from Profile profile join profile.behaviors bi where bi.time > 0")
    List<Profile> findAllByBehavior();

    @Query("select profile from Profile profile join profile.account acc join acc.roles role where role.id= :roleId order by profile.createdAt desc")
    Page<Profile> findByRole(@Param("roleId") Long id, Pageable pageable);
}
