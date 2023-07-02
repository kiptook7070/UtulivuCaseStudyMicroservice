package com.eclectics.usersservice.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequestMapping
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);


    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNo(String phone);


    List<Users> findAllByDeletedFlag(Character deletedFlag);

    List<Users> findAllByDeletedFlagAndVerifiedFlag(Character deletedFlag, Character verifiedFlag);

    Optional<Users> findByEmail(String email);


    @Query(value = "SELECT * FROM users WHERE sn NOT IN (SELECT user_id FROM user_roles)", nativeQuery = true)
    List<Users> allWithoutRoles();
}
