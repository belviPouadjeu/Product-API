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
            summary = "Créer un nouveau produit",
            description = """
        Crée un produit avec les informations suivantes :
        - `name` : nom unique du produit
        - `price` : prix du produit
        - `stockQuantity` : quantité en stock
        
        ⚠️ Le nom du produit doit être unique.
        """
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
              "price": 799.99,
              "stockQuantity": 10
            }
            """)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "400",
                                description = "Entrée invalide",
                                content = @Content(
                                        mediaType = "application/json",
                                        examples = @ExampleObject(value = """
            {
              "code": "BAD_REQUEST",
              "message": "Le champ 'name' est requis"
            }
            """)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "409",
                                description = "Nom du produit déjà existant",
                                content = @Content(
                                        mediaType = "application/json",
                                        examples = @ExampleObject(value = """
            {
              "code": "CONFLICT",
              "message": "Un produit avec le nom 'Smartphone' existe déjà"
            }
            """)
                                )
                        )
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
            summary = "Récupérer tous les produits",
            description = """
            Récupère la liste complète des produits disponibles dans l’inventaire.
            Chaque produit contient un identifiant, un nom, un prix et une quantité de stock.
            
            S’il y a des produits dont le stock est bas, un message d’alerte s’affiche également dans la réponse.
            """
                )
                @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Liste des produits avec alertes éventuelles",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class),
                                        examples = @ExampleObject(value = """
            {
              "content": [
                {
                  "id": 1,
                  "name": "Smartphone",
                  "price": 499.99,
                  "stockQuantity": 4
                },
                {
                  "id": 2,
                  "name": "Tablet",
                  "price": 299.99,
                  "stockQuantity": 15
                },
                {
                  "id": 3,
                  "name": "Desktop PC",
                  "price": 999.99,
                  "stockQuantity": 5
                }
              ],
              "alerts": [
                "⚠️ Stock is low for product: Smartphone"
              ]
            }
            """)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "204",
                                description = "Aucun produit disponible"
                        )
    })
    @GetMapping
    public ResponseEntity<ProductResponse> getAllProducts() {
        ProductResponse productResponse = productService.getAllProducts();
        return new  ResponseEntity<>(productResponse, HttpStatus.OK);

    }

    // =================== UPDATE PRODUCT ======================= /

    @PutMapping("/{productId}")
    @Operation(
            summary = "Mettre à jour un produit existant",
            description = """
            Met à jour les informations d’un produit à partir de son identifiant.
            Le corps de la requête doit contenir un objet JSON avec les champs suivants :
            - `name` (nom unique du produit)
            - `price` (prix du produit)
            - `stockQuantity` (quantité en stock)
            
            Le champ `id` n’est pas requis dans le corps. L’identifiant est passé dans l’URL.
            """
                )
                @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Produit mis à jour avec succès",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class),
                                        examples = @ExampleObject(value = """
            {
              "name": "Smartwatch",
              "price": 199.99,
              "stockQuantity": 10
            }
            """)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "400",
                                description = "Entrée invalide (nom manquant, prix négatif, etc.)",
                                content = @Content(
                                        mediaType = "application/json",
                                        examples = @ExampleObject(value = """
            {
              "code": "BAD_REQUEST",
              "message": "Le champ 'name' est requis."
            }
            """)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "Produit non trouvé pour l’ID fourni",
                                content = @Content(
                                        mediaType = "application/json",
                                        examples = @ExampleObject(value = """
            {
              "code": "NOT_FOUND",
              "message": "Aucun produit trouvé avec l’ID 42"
            }
            """)
                                )
                        )
    })
    public ResponseEntity<?> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO updated = productService.updateProduct(productId, productDTO);
            return ResponseEntity.ok(updated);
        } catch (DuplicateResourceException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        }
    }

    // =================== DELETE PRODUCT ======================= /

    @DeleteMapping("/{productId}")
    @Operation(
            summary = "Supprimer un produit",
            description = """
            Supprime un produit de l’inventaire à partir de son identifiant (`productId`).
            Si aucun produit n’est trouvé avec l’ID spécifié, une erreur 404 est renvoyée.
            
            🔒 Remarque : cette opération est irréversible.
            """
                )
                @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Produit supprimé avec succès",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProductDTO.class),
                                        examples = @ExampleObject(value = """
            {
              "name": "Smartphone",
              "price": 499.99,
              "stockQuantity": 4
            }
            """)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "404",
                                description = "Produit introuvable",
                                content = @Content(
                                        mediaType = "application/json",
                                        examples = @ExampleObject(value = """
            {
              "code": "NOT_FOUND",
              "message": "Aucun produit trouvé avec l’ID 99"
            }
            """)
                                )
                        )
    })
    public ResponseEntity<ProductDTO> deleteProduct(
            @Parameter(description = "ID du produit à supprimer", example = "1")
            @PathVariable Long productId) {

        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }


    // =================== GET LOW PRODUCTS ======================= /

    @GetMapping("/low-stock")
    @Operation(
            summary = "Obtenir les produits en faible stock",
            description = """
            Retourne la liste des produits dont la quantité en stock est inférieure à 5 unités.
            
            📦 Utile pour le réapprovisionnement et la gestion des alertes de stock.
            """
                )
                @ApiResponses(value = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "Produits en faible stock récupérés avec succès",
                                content = @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)),
                                        examples = @ExampleObject(value = """
            [
              {
                "name": "Smartphone",
                "price": 499.99,
                "stockQuantity": 2
              },
              {
                "name": "Laptop",
                "price": 899.99,
                "stockQuantity": 1
              }
            ]
            """)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "204",
                                description = "Aucun produit en faible stock"
                        )
    })
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        List<ProductDTO> lowStockProducts = productService.getLowStockProducts();
        return ResponseEntity.ok(lowStockProducts);
    }


    // =================== EXCEPTIONS ======================= /



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
