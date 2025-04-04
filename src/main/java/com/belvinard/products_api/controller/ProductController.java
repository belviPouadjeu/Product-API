package com.belvinard.products_api.controller;

import com.belvinard.products_api.dto.ProductDTO;
import com.belvinard.products_api.exceptions.ResourceNotFoundException;
import com.belvinard.products_api.response.MyErrorResponses;
import com.belvinard.products_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Créer un nouveau produit",
            description = "Ajoute un produit à l'inventaire. Le nom, le prix et la quantité en stock sont requis."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Produit créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class),
                            examples = @ExampleObject(value = """
                            {
                                "name": "Laptop",
                                "price": 1299.99,
                                "stockQuantity": 20
                            }
                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Champs invalides ou manquants",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                                "message": "Product name cannot be blank"
                            }
                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                                "message": "Une erreur est survenue lors de la création du produit"
                            }
                            """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // ✅ Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MyErrorResponses> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());  // Collect field errors
        }

        MyErrorResponses errorResponse = new MyErrorResponses(
                "BAD_REQUEST",
                "Validation failed. Please correct the errors.",
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Operation(hidden = true)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MyErrorResponses> handleResourceNotFoundException(ResourceNotFoundException ex) {
        MyErrorResponses errorResponse = new MyErrorResponses("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
