package com.jseric.simple_product_rest.controller;

import com.jseric.simple_product_rest.model.fe.CreateProductRequest;
import com.jseric.simple_product_rest.model.fe.CreateProductResponse;
import com.jseric.simple_product_rest.model.fe.FetchProductResponse;
import com.jseric.simple_product_rest.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private static final String BASE_CONTROLLER_PATH = "/api/v1/products";

    private final ProductService productService;

    @Autowired
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }


    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CreateProductResponse> createNewProduct(@RequestBody final CreateProductRequest reqBody) {
        log.info("New POST Request:: " + BASE_CONTROLLER_PATH);
        log.debug("Request body: " + reqBody);

        final ResponseEntity<CreateProductResponse> rsp = productService.createAndSave(reqBody);

        log.info("Response status: " + rsp.getStatusCode());
        log.debug("Response body: " + rsp.getBody());
        return rsp;
    }

    @PutMapping(value = "/{productId}", consumes={"application/json"}, produces={"application/json"})
    public ResponseEntity<CreateProductResponse> updateProduct(
            @PathVariable final String productId,
            @RequestBody final CreateProductRequest reqBody) {
        log.info("New PUT Request:: " + BASE_CONTROLLER_PATH + "/id");
        log.debug("Product ID: " + productId);
        log.debug("Request body: " + reqBody);

        final ResponseEntity<CreateProductResponse> rsp = productService.update(productId, reqBody);

        log.info("Response status: " + rsp.getStatusCode());
        log.debug("Response body: " + rsp.getBody());
        return rsp;
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable final String productId) {
        log.info("New DELETE Request:: " + BASE_CONTROLLER_PATH + "/id");
        log.debug("Product ID: " + productId);

        final ResponseEntity<Object> rsp = productService.delete(productId);

        log.info("Response status: " + rsp.getStatusCode());
        return rsp;
    }

    @GetMapping(produces = {"application/json"})
    public ResponseEntity<FetchProductResponse> fetchAll() {
        log.info("New GET Request:: " + BASE_CONTROLLER_PATH);

        final ResponseEntity<FetchProductResponse> rsp = productService.fetchAll();

        log.info("Response status: " + rsp.getStatusCode());
        log.debug("Response body: " + rsp.getBody());
        return rsp;
    }

    @GetMapping(value = "/{productId}", produces = {"application/json"})
    public ResponseEntity<FetchProductResponse> fetchById(@PathVariable final String productId) {
        log.info("New GET Request:: " + BASE_CONTROLLER_PATH + "/id");
        log.debug("Product ID: " + productId);

        final ResponseEntity<FetchProductResponse> rsp = productService.fetchById(productId);

        log.info("Response status: " + rsp.getStatusCode());
        log.debug("Response body: " + rsp.getBody());
        return rsp;
    }
}
