package com.example.shop.product.domain.repository;

import com.example.shop.product.domain.entity.ProductEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findByName(String name);

    Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<ProductEntity> findByIdIn(List<UUID> productIdList);
}
