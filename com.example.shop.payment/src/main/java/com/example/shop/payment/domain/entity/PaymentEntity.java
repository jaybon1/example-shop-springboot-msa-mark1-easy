package com.example.shop.payment.domain.entity;

import com.example.shop.global.infrastructure.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "PAYMENT")
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private Method method;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "transaction_key")
    private String transactionKey;

    @Builder
    private PaymentEntity(
            UUID id,
            UUID orderId,
            UUID userId,
            Status status,
            Method method,
            Long amount,
            String transactionKey
    ) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status != null ? status : Status.COMPLETED;
        this.method = method;
        this.amount = amount != null ? amount : 0L;
        this.transactionKey = transactionKey;
    }

    public PaymentEntity markCompleted() {
        this.status = Status.COMPLETED;
        return this;
    }

    public PaymentEntity markCancelled() {
        this.status = Status.CANCELLED;
        return this;
    }

    public boolean isOwnedBy(UUID targetUserId) {
        return targetUserId != null && targetUserId.equals(this.userId);
    }

    public void updateDetails(Method method, Long amount, String transactionKey) {
        if (method != null) {
            this.method = method;
        }
        if (amount != null) {
            this.amount = amount;
        }
        if (transactionKey != null) {
            this.transactionKey = transactionKey;
        }
    }

    public enum Status {
        COMPLETED,
        CANCELLED
    }

    public enum Method {
        CARD,
        BANK_TRANSFER,
        MOBILE,
        POINT
    }
}
