package com.jseric.simple_product_rest.controller;

import com.jseric.simple_product_rest.model.fe.CreateProductRequest;
import com.jseric.simple_product_rest.model.fe.CreateProductResponse;
import com.jseric.simple_product_rest.model.fe.FetchProductResponse;
import com.jseric.simple_product_rest.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        final CreateProductResponse rspBody = productService.createNewProduct(reqBody);
        final HttpStatus rspStatus = StringUtils.isEmpty(rspBody.getErrorMessage()) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        log.info("Returning response: " + rspStatus);
        if (rspBody != null) {
            log.debug("Response body: " + rspBody);
        }
        return new ResponseEntity<>(rspBody, rspStatus);
    }

    @PutMapping(value = "/{productId}", consumes={"application/json"}, produces={"application/json"})
    public ResponseEntity<CreateProductResponse> updateProduct(
            @PathVariable final String productId,
            @RequestBody final CreateProductRequest reqBody) {
        log.info("New PUT Request:: " + BASE_CONTROLLER_PATH + "/id");
        log.debug("Product ID: " + productId);
        log.debug("Request body: " + reqBody);

        final CreateProductResponse rspBody = productService.updateProduct(productId, reqBody);
        final HttpStatus rspStatus = StringUtils.isEmpty(rspBody.getErrorMessage()) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        log.info("Returning response: " + rspStatus);
        if (rspBody != null) {
            log.debug("Response body: " + rspBody);
        }
        return new ResponseEntity<>(rspBody, rspStatus);
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable final String productId) {
        log.info("New DELETE Request:: " + BASE_CONTROLLER_PATH + "/id");
        log.debug("Product ID: " + productId);

        final boolean result = productService.deleteProduct(productId);
        final HttpStatus rspStatus = result ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND;
        log.info("Returning response: " + rspStatus);

        return new ResponseEntity<>(rspStatus);
    }

    @GetMapping(produces = {"application/json"})
    public ResponseEntity<FetchProductResponse> fetchAll() {
        log.info("New GET Request:: " + BASE_CONTROLLER_PATH);

        final FetchProductResponse rspBody = productService.fetchAllProducts();
        final HttpStatus rspStatus = StringUtils.isEmpty(rspBody.getErrorMessage()) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;

        log.info("Returning response: " + rspStatus);
        if (rspBody != null) {
            log.debug("Response body: " + rspBody);
        }
        return new ResponseEntity<>(rspBody, rspStatus);
    }

    @GetMapping(value = "/{productId}", produces = {"application/json"})
    public ResponseEntity<FetchProductResponse> fetchById(@PathVariable final String productId) {
        log.info("New GET Request:: " + BASE_CONTROLLER_PATH + "/id");
        log.debug("Product ID: " + productId);

        final FetchProductResponse rspBody = productService.fetchProductById(productId);

        final HttpStatus rspStatus = rspBody.getProducts().size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        log.info("Returning response: " + rspStatus);
        if (rspBody != null) {
            log.debug("Response body: " + rspBody);
        }
        return new ResponseEntity<>(rspBody, rspStatus);
    }
}
