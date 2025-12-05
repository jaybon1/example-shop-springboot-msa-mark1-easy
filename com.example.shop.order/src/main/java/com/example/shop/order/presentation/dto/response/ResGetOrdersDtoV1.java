package com.example.shop.order.presentation.dto.response;

import com.example.shop.order.domain.entity.OrderEntity;
import com.example.shop.order.domain.entity.OrderEntity.Status;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

@Getter
@Builder
public class ResGetOrdersDtoV1 {

    private final OrderPageDto orderPage;

    public static ResGetOrdersDtoV1 of(Page<OrderEntity> orderEntityPage) {
        return ResGetOrdersDtoV1.builder()
                .orderPage(new OrderPageDto(orderEntityPage))
                .build();
    }

    @Getter
    @ToString
    public static class OrderPageDto extends PagedModel<OrderPageDto.OrderDto> {

        public OrderPageDto(Page<OrderEntity> orderEntityPage) {
            super(
                    new PageImpl<>(
                            OrderDto.from(orderEntityPage.getContent()),
                            orderEntityPage.getPageable(),
                            orderEntityPage.getTotalElements()
                    )
            );
        }

        public OrderPageDto(OrderDto... orderDtoArray) {
            super(new PageImpl<>(List.of(orderDtoArray)));
        }

        @Getter
        @Builder
        public static class OrderDto {

            private final String id;
            private final Status status;
            private final Long totalAmount;
            private final Instant createdAt;
            private final Instant updatedAt;

            private static List<OrderDto> from(List<OrderEntity> orderList) {
                return orderList.stream()
                        .map(OrderDto::from)
                        .toList();
            }

            public static OrderDto from(OrderEntity orderEntity) {
                return OrderDto.builder()
                        .id(String.valueOf(orderEntity.getId()))
                        .status(orderEntity.getStatus())
                        .totalAmount(orderEntity.getTotalAmount())
                        .createdAt(orderEntity.getCreatedAt())
                        .updatedAt(orderEntity.getUpdatedAt())
                        .build();
            }
        }
    }
}
