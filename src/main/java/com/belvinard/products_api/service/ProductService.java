package com.belvinard.products_api.service;

import com.belvinard.products_api.dto.ProductDTO;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
}
