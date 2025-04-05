package com.belvinard.products_api.service.impl;

import com.belvinard.products_api.dto.ProductDTO;
import com.belvinard.products_api.dto.ProductResponseDTO;
import com.belvinard.products_api.entity.Product;
import com.belvinard.products_api.exceptions.APIException;
import com.belvinard.products_api.exceptions.DuplicateResourceException;
import com.belvinard.products_api.exceptions.ResourceNotFoundException;
import com.belvinard.products_api.repository.ProductRepository;
import com.belvinard.products_api.response.ProductResponse;
import com.belvinard.products_api.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public ProductResponseDTO createProduct(ProductDTO productDTO) {
        try {
            Product product = modelMapper.map(productDTO, Product.class);
            Product savedProduct = productRepository.save(product);

            String alert = null;
            if (savedProduct.getStockQuantity() < 5) {
                alert = "⚠️ Stock is low for product: " + savedProduct.getName();
                log.warn(alert);
            }

            ProductDTO responseDTO = modelMapper.map(savedProduct, ProductDTO.class);
            return new ProductResponseDTO(responseDTO, alert);

        } catch (DataIntegrityViolationException ex) {
            // on relance une exception métier ici
            throw new DuplicateResourceException("A product with this name already exists.");
        }
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            throw new APIException("No products create until now !!!");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        List<String> alerts = products.stream()
                .filter(p -> p.getStockQuantity() < 5)
                .map(p -> "⚠️ Stock is low for product: " + p.getName())
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setAlerts(alerts);  // Ajout de la liste d'alertes

        return productResponse;
    }



    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStockQuantity(productDTO.getStockQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        if (updatedProduct.getStockQuantity() < 5) {
            log.warn("⚠️ Stock alert (update): Product '{}' has only {} unit(s) in stock.",
                    updatedProduct.getName(), updatedProduct.getStockQuantity());
        }
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));

        productRepository.delete(product);

        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public List<ProductDTO> getLowStockProducts() {
        List<Product> lowStockProducts = productRepository.findByStockQuantityLessThan(5);

        return lowStockProducts.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }



}
