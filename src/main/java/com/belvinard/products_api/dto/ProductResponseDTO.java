package com.belvinard.products_api.dto;


public class ProductResponseDTO {
    private ProductDTO product;
    private String alert;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(ProductDTO product, String alert) {
        this.product = product;
        this.alert = alert;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
}
