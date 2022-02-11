package com.jseric.simple_product_rest.controller;

import com.jseric.simple_product_rest.model.fe.CreateProductRequest;
import com.jseric.simple_product_rest.model.fe.CreateProductResponse;
import com.jseric.simple_product_rest.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @PathVariable String productId,
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
}
