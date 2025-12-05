package com.example.shop.user.presentation.dto.response;

import com.example.shop.user.domain.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetUserDtoV1 {

    private final UserDto user;

    public static ResGetUserDtoV1 of(UserEntity userEntity) {
        return ResGetUserDtoV1.builder()
                .user(UserDto.from(userEntity))
                .build();
    }

    @Getter
    @Builder
    public static class UserDto {

        private final String id;
        private final String username;
        private final String nickname;
        private final String email;

        public static UserDto from(UserEntity userEntity) {
            return UserDto.builder()
                    .id(String.valueOf(userEntity.getId()))
                    .username(userEntity.getUsername())
                    .nickname(userEntity.getNickname())
                    .email(userEntity.getEmail())
                    .build();
        }

    }

}
