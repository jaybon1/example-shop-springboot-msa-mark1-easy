package com.example.shop.product.presentation.dto.response;

import com.example.shop.product.domain.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResPostProductsDtoV1 {

    private final ProductDto product;

    public static ResPostProductsDtoV1 of(ProductEntity productEntity) {
        return ResPostProductsDtoV1.builder()
                .product(ProductDto.from(productEntity))
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductDto {
        private final String id;

        public static ProductDto from(ProductEntity productEntity) {
            return ProductDto.builder()
                    .id(String.valueOf(productEntity.getId()))
                    .build();
        }
    }
}
