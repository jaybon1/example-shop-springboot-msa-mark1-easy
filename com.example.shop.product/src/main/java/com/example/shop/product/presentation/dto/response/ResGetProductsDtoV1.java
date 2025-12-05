package com.example.shop.product.presentation.dto.response;

import com.example.shop.product.domain.entity.ProductEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

@Getter
@Builder
public class ResGetProductsDtoV1 {

    private final ProductPageDto productPage;

    public static ResGetProductsDtoV1 of(Page<ProductEntity> productEntityPage) {
        return ResGetProductsDtoV1.builder()
                .productPage(new ProductPageDto(productEntityPage))
                .build();
    }

    @Getter
    @ToString
    public static class ProductPageDto extends PagedModel<ProductPageDto.ProductDto> {

        public ProductPageDto(Page<ProductEntity> productEntityPage) {
            super(
                    new PageImpl<>(
                            ProductDto.from(productEntityPage.getContent()),
                            productEntityPage.getPageable(),
                            productEntityPage.getTotalElements()
                    )
            );
        }

        public ProductPageDto(ProductDto... productDtoArray) {
            super(
                    new PageImpl<>(
                            List.of(productDtoArray)
                    )
            );
        }

        @Getter
        @Builder
        public static class ProductDto {
            private final String id;
            private final String name;
            private final Long price;
            private final Long stock;

            private static List<ProductDto> from(List<ProductEntity> productList) {
                return productList.stream()
                        .map(ProductDto::from)
                        .toList();
            }

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
}
