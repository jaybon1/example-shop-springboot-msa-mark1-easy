package com.example.shop.product.application.service;

import com.example.shop.product.domain.entity.ProductEntity;
import com.example.shop.product.domain.entity.ProductStockEntity;
import com.example.shop.product.domain.entity.ProductStockEntity.ProductStockType;
import com.example.shop.product.domain.repository.ProductRepository;
import com.example.shop.product.domain.repository.ProductStockRepository;
import com.example.shop.product.presentation.advice.ProductError;
import com.example.shop.product.presentation.advice.ProductException;
import com.example.shop.product.presentation.dto.request.ReqPostInternalProductsReleaseStockDtoV1;
import com.example.shop.product.presentation.dto.request.ReqPostInternalProductsReturnStockDtoV1;
import com.example.shop.product.presentation.dto.request.ReqPostProductsDtoV1;
import com.example.shop.product.presentation.dto.response.ResGetProductDtoV1;
import com.example.shop.product.presentation.dto.response.ResGetProductsDtoV1;
import com.example.shop.product.presentation.dto.response.ResPostProductsDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceV1 {

    private static final String PRODUCT_CACHE_NAME = "product";

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    private final CacheManager cacheManager;

    public ResGetProductsDtoV1 getProducts(Pageable pageable, String name) {
        String normalizedName = normalize(name);
        Page<ProductEntity> productEntityPage = normalizedName == null
                ? productRepository.findAll(pageable)
                : productRepository.findByNameContainingIgnoreCase(normalizedName, pageable);
        return ResGetProductsDtoV1.of(productEntityPage);
    }

    @Cacheable(cacheNames = PRODUCT_CACHE_NAME, key = "#productId")
    public ResGetProductDtoV1 getProduct(UUID productId) {
        return ResGetProductDtoV1.of(findProductById(productId));
    }

    @Transactional
    public ResPostProductsDtoV1 postProducts(ReqPostProductsDtoV1 reqDto) {
        ReqPostProductsDtoV1.ProductDto reqProduct = reqDto.getProduct();
        String normalizedName = normalize(reqProduct.getName());
        if (normalizedName == null) {
            throw new ProductException(ProductError.PRODUCT_BAD_REQUEST);
        }
        validateDuplicatedName(normalizedName, Optional.empty());
        ProductEntity newProductEntity = ProductEntity.builder()
                .name(normalizedName)
                .price(reqProduct.getPrice())
                .stock(reqProduct.getStock())
                .build();
        ProductEntity savedProductEntity = productRepository.save(newProductEntity);
        return ResPostProductsDtoV1.of(savedProductEntity);
    }

    @Transactional
    public void postInternalProductsReleaseStock(ReqPostInternalProductsReleaseStockDtoV1 reqDto) {
        if (productStockRepository.existsByOrderIdAndType(reqDto.getOrder().getOrderId(), ProductStockType.RELEASE)) {
            throw new ProductException(ProductError.PRODUCT_BAD_REQUEST);
        }

        List<UUID> productIds = reqDto.getProductStocks().stream()
                .map(ReqPostInternalProductsReleaseStockDtoV1.ProductStockDto::getProductId)
                .toList();

        List<ProductEntity> productEntityList = productRepository.findByIdIn(productIds);

        reqDto.getProductStocks().forEach(productStockDto -> {
            productEntityList.stream().filter(productEntity -> productEntity.getId().equals(productStockDto.getProductId())).findFirst()
                    .ifPresent(productEntity -> {
                        if (productEntity.getStock() < productStockDto.getQuantity()) {
                            throw new ProductException(ProductError.PRODUCT_BAD_REQUEST);
                        }
                        ProductEntity updatedProductEntity = productEntity.update(null, null, productEntity.getStock() - productStockDto.getQuantity());
                        productRepository.save(updatedProductEntity);
                    });
        });

        reqDto.getProductStocks().forEach(productStockDto ->
                productStockRepository.save(
                        ProductStockEntity.builder()
                                .productId(productStockDto.getProductId())
                                .orderId(reqDto.getOrder().getOrderId())
                                .quantity(productStockDto.getQuantity())
                                .type(ProductStockType.RELEASE)
                                .build()
                )
        );

        evictProductCache(productIds);
    }

    @Transactional
    public void postInternalProductsReturnStock(ReqPostInternalProductsReturnStockDtoV1 reqDto) {
        if (productStockRepository.existsByOrderIdAndType(reqDto.getOrder().getOrderId(), ProductStockType.RETURN)) {
            throw new ProductException(ProductError.PRODUCT_BAD_REQUEST);
        }

        List<ProductStockEntity> productStockEntityList = productStockRepository.findByOrderId(reqDto.getOrder().getOrderId());
        List<UUID> productIds = productStockEntityList.stream()
                .map(ProductStockEntity::getProductId)
                .toList();

        List<ProductEntity> productEntityList = productRepository.findByIdIn(productIds);

        productStockEntityList.forEach(productStockEntity -> {
            productEntityList.stream().filter(productEntity -> productEntity.getId().equals(productStockEntity.getProductId())).findFirst()
                    .ifPresent(productEntity -> {
                        ProductEntity updatedProductEntity = productEntity.update(null, null, productEntity.getStock() + productStockEntity.getQuantity());
                        productRepository.save(updatedProductEntity);
                    });
        });

        productStockEntityList.forEach(productStockEntity ->
                productStockRepository.save(
                        ProductStockEntity.builder()
                                .productId(productStockEntity.getProductId())
                                .orderId(reqDto.getOrder().getOrderId())
                                .quantity(productStockEntity.getQuantity())
                                .type(ProductStockType.RETURN)
                                .build()
                )
        );

        evictProductCache(productIds);
    }

    private void evictProductCache(List<UUID> productIds) {
        Cache cache = cacheManager.getCache(PRODUCT_CACHE_NAME);
        if (cache == null) {
            return;
        }
        productIds.forEach(cache::evict);
    }

    private void validateDuplicatedName(String name, Optional<UUID> excludeId) {
        productRepository.findByName(name)
                .ifPresent(productEntity -> {
                    if (excludeId.isEmpty() || !productEntity.getId().equals(excludeId.get())) {
                        throw new ProductException(ProductError.PRODUCT_NAME_DUPLICATED);
                    }
                });
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ProductEntity findProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductError.PRODUCT_CAN_NOT_FOUND));
    }

}
