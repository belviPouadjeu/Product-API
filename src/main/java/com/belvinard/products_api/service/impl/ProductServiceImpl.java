package com.belvinard.products_api.service.impl;

import com.belvinard.products_api.dto.ProductDTO;
import com.belvinard.products_api.entity.Product;
import com.belvinard.products_api.exceptions.APIException;
import com.belvinard.products_api.exceptions.ResourceNotFoundException;
import com.belvinard.products_api.repository.ProductRepository;
import com.belvinard.products_api.response.ProductResponse;
import com.belvinard.products_api.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Convert ProductDTO to Product
        Product product = modelMapper.map(productDTO, Product.class);

        // Fnd Product from the database
        Product productFromBd = productRepository.findByName(product.getName());
        if (productFromBd != null) {
            throw new ResourceNotFoundException("Product with name " + product.getName() + " already exists");
        }

        Product savedProduct = productRepository.save(product);

        // Convert Product to ProductDTO
        return modelMapper.map(savedProduct, ProductDTO.class);
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

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;


    }
}
