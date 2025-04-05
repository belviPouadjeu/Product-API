package com.belvinard.products_api.response;

import com.belvinard.products_api.dto.ProductDTO;

import java.util.List;

public class ProductResponse {
    List<ProductDTO> content;
    private List<String> alerts;

    public ProductResponse() {
    }

    public ProductResponse(List<ProductDTO> content, List<String> alerts) {
        this.content = content;
        this.alerts = alerts;
    }

    public List<ProductDTO> getContent() {
        return content;
    }

    public void setContent(List<ProductDTO> content) {
        this.content = content;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }
}
