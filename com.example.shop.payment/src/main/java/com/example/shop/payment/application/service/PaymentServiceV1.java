package com.example.shop.payment.application.service;

import com.example.shop.payment.application.client.OrderClientV1;
import com.example.shop.payment.domain.entity.PaymentEntity;
import com.example.shop.payment.domain.repository.PaymentRepository;
import com.example.shop.payment.infrastructure.resttemplate.order.dto.request.ReqPostInternalOrderCompleteDtoV1;
import com.example.shop.payment.presentation.advice.PaymentError;
import com.example.shop.payment.presentation.advice.PaymentException;
import com.example.shop.payment.presentation.dto.request.ReqPostPaymentsDtoV1;
import com.example.shop.payment.presentation.dto.response.ResGetPaymentDtoV1;
import com.example.shop.payment.presentation.dto.response.ResPostPaymentsDtoV1;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceV1 {

    private final PaymentRepository paymentRepository;
    private final OrderClientV1 orderClientV1;

    public ResGetPaymentDtoV1 getPayment(UUID authUserId, UUID paymentId) {
        PaymentEntity paymentEntity = findPayment(paymentId);
        validateOwnership(paymentEntity, authUserId);
        return ResGetPaymentDtoV1.of(paymentEntity);
    }

    @Transactional
    public ResPostPaymentsDtoV1 postPayments(UUID authUserId, String accessJwt, ReqPostPaymentsDtoV1 reqDto) {
        // TODO 결제 처리 로직 추가
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .orderId(reqDto.getPayment().getOrderId())
                .userId(authUserId)
                .status(PaymentEntity.Status.COMPLETED)
                .method(reqDto.getPayment().getMethod())
                .amount(reqDto.getPayment().getAmount())
                .transactionKey(UUID.randomUUID().toString())
                .build();
        PaymentEntity savedPaymentEntity = paymentRepository.save(paymentEntity);
        orderClientV1.postInternalOrdersComplete(
                savedPaymentEntity.getOrderId(),
                ReqPostInternalOrderCompleteDtoV1.builder()
                        .payment(
                                ReqPostInternalOrderCompleteDtoV1.PaymentDto.builder()
                                        .paymentId(savedPaymentEntity.getId())
                                        .amount(savedPaymentEntity.getAmount())
                                        .method(savedPaymentEntity.getMethod())
                                        .build()
                        )
                        .build(),
                accessJwt
        );
        return ResPostPaymentsDtoV1.of(savedPaymentEntity);
    }

    @Transactional
    public void postInternalPaymentsCancel(UUID paymentId) {
        PaymentEntity paymentEntity = findPayment(paymentId);
        if (PaymentEntity.Status.CANCELLED.equals(paymentEntity.getStatus())) {
            throw new PaymentException(PaymentError.PAYMENT_ALREADY_CANCELLED);
        }
        // TODO 결제 취소 처리 로직 추가
        PaymentEntity cancelledPaymentEntity = paymentEntity.markCancelled();
        paymentRepository.save(cancelledPaymentEntity);
    }

    private PaymentEntity findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentError.PAYMENT_NOT_FOUND));
    }

    private void validateOwnership(PaymentEntity paymentEntity, UUID authUserId) {
        if (!paymentEntity.isOwnedBy(authUserId)) {
            throw new PaymentException(PaymentError.PAYMENT_FORBIDDEN);
        }
    }

}
