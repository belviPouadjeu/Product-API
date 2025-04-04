package com.belvinard.products_api.service;

import com.belvinard.products_api.dto.ProductDTO;
import com.belvinard.products_api.response.ProductResponse;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductResponse getAllProducts();
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    //ProductDTO deleteProduct(ProductDTO productDTO, Long productId);
    ProductDTO deleteProduct(Long productId);
}
