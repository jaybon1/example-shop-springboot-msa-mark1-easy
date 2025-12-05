package com.example.shop.product.domain.repository;

import com.example.shop.product.domain.entity.ProductStockEntity;
import com.example.shop.product.domain.entity.ProductStockEntity.ProductStockType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStockEntity, UUID> {

    boolean existsByProductIdAndOrderIdAndType(UUID productId, UUID orderId, ProductStockType type);

    List<ProductStockEntity> findByOrderId(UUID orderId);

    boolean existsByOrderIdAndType(UUID orderId, ProductStockType type);

    List<ProductStockEntity> findByIdIn(List<UUID> productStockIdList);
}
