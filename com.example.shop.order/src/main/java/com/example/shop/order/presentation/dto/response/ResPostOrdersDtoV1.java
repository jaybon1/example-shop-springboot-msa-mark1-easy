package com.example.shop.order.presentation.dto.response;

import com.example.shop.order.domain.entity.OrderEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResPostOrdersDtoV1 {

    private final OrderDto order;

    public static ResPostOrdersDtoV1 of(OrderEntity orderEntity) {
        return ResPostOrdersDtoV1.builder()
                .order(OrderDto.from(orderEntity))
                .build();
    }

    @Getter
    @Builder
    public static class OrderDto {

        private final String id;

        public static OrderDto from(OrderEntity orderEntity) {
            return OrderDto.builder()
                    .id(String.valueOf(orderEntity.getId()))
                    .build();
        }
    }
}
