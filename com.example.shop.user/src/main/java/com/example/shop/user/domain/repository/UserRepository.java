package com.example.shop.user.domain.repository;

import com.example.shop.user.domain.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE (:username IS NULL OR lower(u.username) LIKE lower(concat('%', :username, '%')))
              AND (:nickname IS NULL OR lower(u.nickname) LIKE lower(concat('%', :nickname, '%')))
              AND (:email IS NULL OR lower(u.email) LIKE lower(concat('%', :email, '%')))
            """)
    Page<UserEntity> searchUsers(
            @Param("username") String username,
            @Param("nickname") String nickname,
            @Param("email") String email,
            Pageable pageable
    );
}
