package com.eclectics.usersservice.Roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findById(Long id);

    Optional<Role> findByRoleCode(String roleCode);

    Optional<Role> findByName(String name);

    List<Role> findByDeletedFlag(Character flag);

    List<Role> findByDeletedFlagAndVerifiedFlag(Character deleted, Character verified);

}
