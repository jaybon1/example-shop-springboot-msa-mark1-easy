package com.example.shop.user.presentation.dto.response;

import com.example.shop.user.domain.entity.UserEntity;
import com.example.shop.user.domain.entity.UserRoleEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

@Getter
@Builder
public class ResGetUsersDtoV1 {

    private final UserPageDto userPage;

    public static ResGetUsersDtoV1 of(Page<UserEntity> userEntityPage) {
        return ResGetUsersDtoV1.builder()
                .userPage(new UserPageDto(userEntityPage))
                .build();
    }

    @Getter
    @ToString
    public static class UserPageDto extends PagedModel<UserPageDto.UserDto> {

        public UserPageDto(Page<UserEntity> userEntityPage) {
            super(
                    new PageImpl<>(
                            UserDto.from(userEntityPage.getContent()),
                            userEntityPage.getPageable(),
                            userEntityPage.getTotalElements()
                    )
            );
        }

        public UserPageDto(UserDto... userDtoArray) {
            super(
                    new PageImpl<>(
                            List.of(userDtoArray)
                    )
            );
        }

        @Getter
        @Builder
        public static class UserDto {

            private final String id;
            private final String username;
            private final String nickname;
            private final String email;
            private final List<String> roleList;

            private static List<UserDto> from(List<UserEntity> userList) {
                return userList.stream()
                        .map(UserDto::from)
                        .toList();
            }

            public static UserDto from(UserEntity userEntity) {
                return UserDto.builder()
                        .id(String.valueOf(userEntity.getId()))
                        .username(userEntity.getUsername())
                        .nickname(userEntity.getNickname())
                        .email(userEntity.getEmail())
                        .roleList(
                                userEntity.getUserRoleList()
                                        .stream()
                                        .map(UserRoleEntity::getRole)
                                        .map(Enum::name)
                                        .toList()
                        )
                        .build();
            }
        }
    }
}
