package com.example.shop.payment.infrastructure.resttemplate.order.dto.request;

import com.example.shop.payment.domain.entity.PaymentEntity;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqPostInternalOrderCompleteDtoV1 {

    private final PaymentDto payment;

    @Getter
    @Builder
    public static class PaymentDto {

        private final UUID paymentId;
        private final Long amount;
        private final PaymentEntity.Method method;
    }
}
