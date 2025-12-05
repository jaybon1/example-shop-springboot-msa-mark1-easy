package com.example.shop.payment.domain.repository;

import com.example.shop.payment.domain.entity.PaymentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
