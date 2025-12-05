package com.example.shop.order.presentation.dto.response;

import com.example.shop.order.domain.entity.OrderEntity;
import com.example.shop.order.domain.entity.OrderEntity.Status;
import com.example.shop.order.domain.entity.OrderEntity.OrderPaymentMethod;
import com.example.shop.order.domain.entity.OrderEntity.OrderPaymentStatus;
import com.example.shop.order.domain.entity.OrderItemEntity;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetOrderDtoV1 {

    private final OrderDto order;

    public static ResGetOrderDtoV1 of(OrderEntity orderEntity) {
        return ResGetOrderDtoV1.builder()
                .order(OrderDto.from(orderEntity))
                .build();
    }

    @Getter
    @Builder
    public static class OrderDto {

        private final String id;
        private final Status status;
        private final Long totalAmount;
        private final Instant createdAt;
        private final Instant updatedAt;
        private final List<OrderItemDto> orderItemList;
        private final PaymentDto payment;

        public static OrderDto from(OrderEntity orderEntity) {
            return OrderDto.builder()
                    .id(String.valueOf(orderEntity.getId()))
                    .status(orderEntity.getStatus())
                    .totalAmount(orderEntity.getTotalAmount())
                    .createdAt(orderEntity.getCreatedAt())
                    .updatedAt(orderEntity.getUpdatedAt())
                    .orderItemList(OrderItemDto.from(orderEntity.getOrderItemList()))
                    .payment(PaymentDto.from(orderEntity))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrderItemDto {

        private final String id;
        private final String productId;
        private final String productName;
        private final Long unitPrice;
        private final Long quantity;
        private final Long lineTotal;

        private static List<OrderItemDto> from(List<OrderItemEntity> orderItemEntityList) {
            return orderItemEntityList.stream()
                    .map(OrderItemDto::from)
                    .toList();
        }

        public static OrderItemDto from(OrderItemEntity orderItemEntity) {
            if (orderItemEntity == null) {
                return null;
            }
            return OrderItemDto.builder()
                    .id(String.valueOf(orderItemEntity.getId()))
                    .productId(String.valueOf(orderItemEntity.getProductId()))
                    .productName(orderItemEntity.getProductName())
                    .unitPrice(orderItemEntity.getUnitPrice())
                    .quantity(orderItemEntity.getQuantity())
                    .lineTotal(orderItemEntity.getLineTotal())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PaymentDto {

        private final String id;
        private final OrderPaymentStatus status;
        private final OrderPaymentMethod method;
        private final Long amount;

        public static PaymentDto from(OrderEntity orderEntity) {
            if (orderEntity == null || orderEntity.getPaymentId() == null) {
                return null;
            }
            return PaymentDto.builder()
                    .id(String.valueOf(orderEntity.getPaymentId()))
                    .status(orderEntity.getPaymentStatus())
                    .method(orderEntity.getPaymentMethod())
                    .amount(orderEntity.getPaymentAmount())
                    .build();
        }
    }
}
