package com.belvinard.products_api.service;

import com.belvinard.products_api.dto.ProductDTO;
import com.belvinard.products_api.dto.ProductResponseDTO;
import com.belvinard.products_api.response.ProductResponse;

import java.util.List;

public interface ProductService {
    //ProductDTO createProduct(ProductDTO productDTO);
    ProductResponseDTO createProduct(ProductDTO productDTO);
    ProductResponse getAllProducts();
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    //ProductDTO deleteProduct(ProductDTO productDTO, Long productId);
    ProductDTO deleteProduct(Long productId);

    List<ProductDTO> getLowStockProducts();
}
