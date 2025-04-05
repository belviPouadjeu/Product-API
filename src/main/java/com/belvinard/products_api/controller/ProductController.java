package com.belvinard.products_api.controller;

import com.belvinard.products_api.dto.ProductDTO;
import com.belvinard.products_api.dto.ProductResponseDTO;
import com.belvinard.products_api.exceptions.APIException;
import com.belvinard.products_api.exceptions.DuplicateResourceException;
import com.belvinard.products_api.exceptions.ResourceNotFoundException;
import com.belvinard.products_api.response.MyErrorResponses;
import com.belvinard.products_api.response.ProductResponse;
import com.belvinard.products_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =================== CREATE PRODUCT ======================= /

    @Operation(
            summary = "Create a new product",
            description = "Creates a product with name, price and stock quantity. Name must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Product name already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductResponseDTO response = productService.createProduct(productDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DuplicateResourceException ex) {
            // Retourne un message clair à Swagger
            Map<String, String> error = new HashMap<>();
            error.put("status", "CONFLICT");
            error.put("message", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "INTERNAL_SERVER_ERROR");
            error.put("message", "Unexpected error occurred");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =================== GET ALL PRODUCTS ======================= /
    @Operation(
            summary = "Get all products",
            description = "Returns a list of all products in the inventory."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of products",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No products available"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "List of products",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ProductResponse> getAllProducts() {
        ProductResponse productResponse = productService.getAllProducts();
        return new  ResponseEntity<>(productResponse, HttpStatus.OK);

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

    // =================== UPDATE PRODUCT ======================= /

    @PutMapping("/{productId}")
    @Operation(
            summary = "Update an existing product",
            description = "Updates the product details by ID. Requires name, price, and stock quantity."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID of the product to update", example = "1")
            @PathVariable Long productId,

            @Valid @RequestBody ProductDTO productDTO) {

        ProductDTO updated = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // =================== DELETE PRODUCT ======================= /

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by its ID. Throws 404 if the product is not found."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(
            @Parameter(description = "ID of the product to delete", example = "1")
            @PathVariable Long productId) {

        ProductDTO productDTO =  productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    // =================== GET LOW PRODUCTS ======================= /

    @Operation(
            summary = "Get low stock products",
            description = "Returns a list of products with stock less than 5"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Low stock products fetched successfully")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        List<ProductDTO> lowStockProducts = productService.getLowStockProducts();
        return ResponseEntity.ok(lowStockProducts);
    }

    // =================== EXCEPTIONS ======================= /

    @Operation(hidden = true)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MyErrorResponses> handleResourceNotFoundException(ResourceNotFoundException ex) {
        MyErrorResponses errorResponse = new MyErrorResponses("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<MyErrorResponses> myAPIException(APIException ex) {
        MyErrorResponses errorResponse = new MyErrorResponses("BAD_REQUEST", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


}
