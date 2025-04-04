package com.belvinard.products_api.repository;

import com.belvinard.products_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);


    Optional<Product> findByProductId(Long productId);
}