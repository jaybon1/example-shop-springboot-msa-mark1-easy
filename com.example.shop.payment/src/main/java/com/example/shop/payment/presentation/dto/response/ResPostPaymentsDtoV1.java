package com.example.shop.payment.presentation.dto.response;

import com.example.shop.payment.domain.entity.PaymentEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResPostPaymentsDtoV1 {

    private final PaymentDto payment;

    public static ResPostPaymentsDtoV1 of(PaymentEntity paymentEntity) {
        return ResPostPaymentsDtoV1.builder()
                .payment(PaymentDto.from(paymentEntity))
                .build();
    }

    @Getter
    @Builder
    public static class PaymentDto {
        private final String id;

        public static PaymentDto from(PaymentEntity paymentEntity) {
            return PaymentDto.builder()
                    .id(String.valueOf(paymentEntity.getId()))
                    .build();
        }
    }
}
