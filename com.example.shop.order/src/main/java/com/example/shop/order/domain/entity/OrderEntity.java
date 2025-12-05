package com.example.shop.order.domain.entity;

import com.example.shop.global.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "`ORDER`")
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItemList;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private OrderPaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private OrderPaymentMethod paymentMethod;

    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Builder
    private OrderEntity(
            UUID id,
            UUID userId,
            Status status,
            Long totalAmount,
            List<OrderItemEntity> orderItemList,
            UUID paymentId,
            OrderPaymentStatus paymentStatus,
            OrderPaymentMethod paymentMethod,
            Long paymentAmount
    ) {
        this.id = id;
        this.userId = userId;
        this.status = status != null ? status : Status.CREATED;
        this.totalAmount = totalAmount != null ? totalAmount : 0L;
        this.orderItemList = orderItemList != null ? new ArrayList<>(orderItemList) : new ArrayList<>();
        this.orderItemList.forEach(item -> item.setOrder(this));
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
    }

    public List<OrderItemEntity> getOrderItemList() {
        return orderItemList == null ? List.of() : Collections.unmodifiableList(orderItemList);
    }

    public OrderEntity addOrderItem(OrderItemEntity orderItemEntity) {
        if (orderItemEntity == null) {
            return this;
        }
        orderItemEntity.setOrder(this);
        this.orderItemList.add(orderItemEntity);
        return this;
    }

    public OrderEntity updateTotalAmount(Long totalAmount) {
        if (totalAmount != null) {
            this.totalAmount = totalAmount;
        }
        return this;
    }

    public OrderEntity markCancelled() {
        this.status = Status.CANCELLED;
        return this;
    }

    public OrderEntity markPaid(UUID paymentId, OrderPaymentStatus paymentStatus, OrderPaymentMethod paymentMethod, Long paymentAmount) {
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
        this.status = Status.PAID;
        return this;
    }

    public OrderEntity assignPayment(UUID paymentId, OrderPaymentStatus paymentStatus, OrderPaymentMethod paymentMethod, Long paymentAmount) {
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
        return this;
    }

    public boolean isOwnedBy(UUID targetUserId) {
        return targetUserId != null && targetUserId.equals(this.userId);
    }

    public enum Status {
        CREATED,
        PAID,
        CANCELLED
    }

    public enum OrderPaymentStatus {
        REQUESTED,
        COMPLETED,
        CANCELLED
    }

    public enum OrderPaymentMethod {
        CARD,
        BANK_TRANSFER,
        MOBILE,
        POINT
    }
}
