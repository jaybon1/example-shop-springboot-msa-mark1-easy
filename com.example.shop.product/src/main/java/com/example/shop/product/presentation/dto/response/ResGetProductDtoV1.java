package com.example.shop.product.presentation.dto.response;

import com.example.shop.product.domain.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResGetProductDtoV1 {

    private final ProductDto product;

    public static ResGetProductDtoV1 of(ProductEntity productEntity) {
        return ResGetProductDtoV1.builder()
                .product(ProductDto.from(productEntity))
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductDto {
        private final String id;
        private final String name;
        private final Long price;
        private final Long stock;

        public static ProductDto from(ProductEntity productEntity) {
            return ProductDto.builder()
                    .id(String.valueOf(productEntity.getId()))
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .stock(productEntity.getStock())
                    .build();
        }
    }
}
