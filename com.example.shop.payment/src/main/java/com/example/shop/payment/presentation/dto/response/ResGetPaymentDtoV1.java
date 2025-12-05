package com.example.shop.payment.presentation.dto.response;

import com.example.shop.payment.domain.entity.PaymentEntity;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResGetPaymentDtoV1 {

    private final PaymentDto payment;

    public static ResGetPaymentDtoV1 of(PaymentEntity paymentEntity) {
        return ResGetPaymentDtoV1.builder()
                .payment(PaymentDto.from(paymentEntity))
                .build();
    }

    @Getter
    @Builder
    public static class PaymentDto {
        private final String id;
        private final String status;
        private final String method;
        private final Long amount;
        private final Instant approvedAt;
        private final String transactionKey;
        private final String orderId;

        public static PaymentDto from(PaymentEntity paymentEntity) {
            return PaymentDto.builder()
                    .id(String.valueOf(paymentEntity.getId()))
                    .status(paymentEntity.getStatus().toString())
                    .method(paymentEntity.getMethod().toString())
                    .amount(paymentEntity.getAmount())
                    .approvedAt(paymentEntity.getCreatedAt())
                    .transactionKey(paymentEntity.getTransactionKey())
                    .orderId(paymentEntity.getOrderId().toString())
                    .build();
        }
    }
}
