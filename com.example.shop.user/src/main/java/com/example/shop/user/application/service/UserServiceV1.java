package com.example.shop.user.application.service;

import com.example.shop.user.application.cache.AuthCache;
import com.example.shop.user.domain.entity.UserEntity;
import com.example.shop.user.domain.entity.UserRoleEntity;
import com.example.shop.user.domain.repository.UserRepository;
import com.example.shop.user.presentation.advice.UserError;
import com.example.shop.user.presentation.advice.UserException;
import com.example.shop.user.presentation.dto.response.ResGetUsersDtoV1;
import com.example.shop.user.presentation.dto.response.ResGetUserDtoV1;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceV1 {

    private final UserRepository userRepository;

    private final AuthCache authCache;

    public ResGetUsersDtoV1 getUsers(
            UUID authUserId,
            List<String> authUserRoleList,
            Pageable pageable,
            String username,
            String nickname,
            String email
    ) {
        if (!isAdmin(authUserRoleList) && !isManager(authUserRoleList)) {
            throw new UserException(UserError.USER_FORBIDDEN);
        }

        String normalizedUsername = normalize(username);
        String normalizedNickname = normalize(nickname);
        String normalizedEmail = normalize(email);

        Pageable resolvedPageable = applyDefaultSort(pageable);
        Page<UserEntity> userEntityPage = userRepository.searchUsers(normalizedUsername, normalizedNickname, normalizedEmail, resolvedPageable);

        return ResGetUsersDtoV1.of(userEntityPage);
    }

    public ResGetUserDtoV1 getUser(UUID authUserId, List<String> authUserRoleList, UUID userId) {
        UserEntity userEntity = getUserOrThrow(userId);
        validateAccess(authUserId, authUserRoleList, userEntity);
        return ResGetUserDtoV1.of(userEntity);
    }

    @Transactional
    public void deleteUser(UUID authUserId, List<String> authUserRoleList, UUID userId) {
        UserEntity userEntity = getUserOrThrow(userId);
        validateAccess(authUserId, authUserRoleList, userEntity);
        boolean targetIsAdmin = userEntity.getUserRoleList().stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(role -> role == UserRoleEntity.Role.ADMIN);
        if (targetIsAdmin) {
            throw new UserException(UserError.USER_BAD_REQUEST);
        }
        userEntity.markDeleted(Instant.now(), authUserId);
        authCache.denyBy(String.valueOf(userId), Instant.now().getEpochSecond());
        userRepository.save(userEntity);
    }

    private void validateAccess(UUID authUserId, List<String> authUserRoleList, UserEntity targetUserEntity) {
        boolean targetIsAdmin = targetUserEntity.getUserRoleList().stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(role -> role == UserRoleEntity.Role.ADMIN);
        if (targetIsAdmin && !isAdmin(authUserRoleList)) {
            throw new UserException(UserError.USER_BAD_REQUEST);
        }

        if ((authUserId != null && authUserId.equals(targetUserEntity.getId()))
                || isAdmin(authUserRoleList)
                || isManager(authUserRoleList)) {
            return;
        }
        throw new UserException(UserError.USER_BAD_REQUEST);
    }

    private UserEntity getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserError.USER_CAN_NOT_FOUND));
    }

    private boolean isAdmin(List<String> authUserRoleList) {
        return !CollectionUtils.isEmpty(authUserRoleList)
                && authUserRoleList.contains(UserRoleEntity.Role.ADMIN.toString());
    }

    private boolean isManager(List<String> authUserRoleList) {
        return !CollectionUtils.isEmpty(authUserRoleList)
                && authUserRoleList.contains(UserRoleEntity.Role.MANAGER.toString());
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Pageable applyDefaultSort(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return Pageable.unpaged();
        }
        if (pageable.getSort().isSorted()) {
            return pageable;
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
    }

}
