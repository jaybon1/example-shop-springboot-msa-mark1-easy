package com.example.shop.user.domain.entity;

import com.example.shop.global.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "`USER`")
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "jwt_validator")
    private Long jwtValidator;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoleEntity> userRoleList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSocialEntity> userSocialList;

    @Builder
    private UserEntity(
            UUID id,
            String username,
            String password,
            String nickname,
            String email,
            Long jwtValidator,
            List<UserRoleEntity> userRoleList,
            List<UserSocialEntity> userSocialList
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.jwtValidator = jwtValidator != null ? jwtValidator : 0L;
        this.userRoleList = userRoleList != null ? new ArrayList<>(userRoleList) : new ArrayList<>();
        this.userRoleList.forEach(role -> role.setUser(this));
        this.userSocialList = userSocialList != null ? new ArrayList<>(userSocialList) : new ArrayList<>();
        this.userSocialList.forEach(social -> social.setUser(this));
    }

    public boolean hasRole(UserRoleEntity.Role role) {
        if (role == null) {
            return false;
        }
        return getUserRoleList().stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(role::equals);
    }

    public UserEntity updateJwtValidator(Long jwtValidator) {
        if (jwtValidator != null) {
            this.jwtValidator = jwtValidator;
        }
        return this;
    }

    public void markDeleted(Instant deletedAt, UUID userId) {
        super.markDeleted(deletedAt, userId);
    }

    public List<UserRoleEntity> getUserRoleList() {
        return userRoleList == null ? List.of() : Collections.unmodifiableList(userRoleList);
    }

    public List<UserSocialEntity> getUserSocialList() {
        return userSocialList == null ? List.of() : Collections.unmodifiableList(userSocialList);
    }

    public UserEntity addRole(UserRoleEntity userRole) {
        if (userRole == null) {
            return this;
        }
        userRole.setUser(this);
        this.userRoleList.add(userRole);
        return this;
    }

    public UserEntity addSocial(UserSocialEntity userSocial) {
        if (userSocial == null) {
            return this;
        }
        userSocial.setUser(this);
        this.userSocialList.add(userSocial);
        return this;
    }

    public boolean isDeleted() {
        return getDeletedAt() != null;
    }

    public void updatePassword(String encodedPassword) {
        if (encodedPassword != null) {
            this.password = encodedPassword;
        }
    }
}
