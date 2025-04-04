package com.belvinard.products_api.response;

import com.belvinard.products_api.dto.ProductDTO;

import java.util.List;

public class ProductResponse {
    List<ProductDTO> content;

    public ProductResponse() {
    }

    public ProductResponse(List<ProductDTO> content) {
        this.content = content;
    }

    public List<ProductDTO> getContent() {
        return content;
    }

    public void setContent(List<ProductDTO> content) {
        this.content = content;
    }
}
