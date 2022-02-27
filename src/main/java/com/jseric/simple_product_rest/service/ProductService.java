package com.jseric.simple_product_rest.service;

import com.jseric.simple_product_rest.model.fe.CreateProductRequest;
import com.jseric.simple_product_rest.model.fe.CreateProductResponse;
import com.jseric.simple_product_rest.model.fe.FetchProductResponse;
import com.jseric.simple_product_rest.model.fe.ProductWrapper;
import com.jseric.simple_product_rest.model.product.Product;
import com.jseric.simple_product_rest.repository.ProductRepository;
import com.jseric.simple_product_rest.service.hnb.CurrencyConversionService;
import com.jseric.simple_product_rest.service.validation.ProductValidationService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductValidationService productValidationService;
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public ProductService(
            final ProductRepository productRepository,
            final ProductValidationService productValidationService,
            final CurrencyConversionService currencyConversionService) {
        this.productRepository = productRepository;
        this.productValidationService = productValidationService;
        this.currencyConversionService = currencyConversionService;
    }

    /**
     * Create and save new Product
     * @param reqBody {@link com.jseric.simple_product_rest.model.fe.CreateProductRequest}
     * @return {@link org.springframework.http.ResponseEntity}&lt;{@link com.jseric.simple_product_rest.model.fe.CreateProductResponse}&gt;
     */
    public ResponseEntity<CreateProductResponse> createAndSave(final CreateProductRequest reqBody) {
        final CreateProductResponse rspBody = new CreateProductResponse();

        if (reqBody == null || reqBody.getProduct() == null) {
            log.error("Request body is empty!");
            rspBody.setErrorMessage("request body is empty;");
            return new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST);
        }

        final ProductWrapper requestData = reqBody.getProduct();

        // Validate data
        log.info("Validating new Product data");
        final String errorMessage = productValidationService.validateCreateUpdateRequest(requestData);
        if (!errorMessage.isEmpty()) {
            log.info("Data validation failed. Errors exist.");
            log.trace("errorMessage :: " + errorMessage);
            rspBody.setErrorMessage(errorMessage);
            return new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST);
        }

        // Check that code is unique (i.e. it doesn't already exist in system)
        log.info("Validating code uniqueness");
        if (productRepository.doesExistByCode(requestData.getCode())) {
            log.info("Code is not unique. Another object with same code field was found");
            rspBody.setErrorMessage("another object with same code field already exists in system;");
            return new ResponseEntity<>(rspBody, HttpStatus.CONFLICT);
        }

        // Fetch price in EUR currency
        log.info("Converting price in HRK to EUR");
        final BigDecimal priceEur = currencyConversionService.convertHrkToEur(requestData.getPriceHrk());

        // Create new Product
        Product product = new Product(
                requestData.getCode(),
                requestData.getName(),
                requestData.getPriceHrk(),
                priceEur,
                requestData.getDescription(),
                requestData.getIsAvailable());

        // Save product
        log.info("Saving new product");
        log.debug(product.toString());
        product = productRepository.save(product);

        rspBody.setProduct(new ProductWrapper(product));
        return new ResponseEntity<>(rspBody, HttpStatus.CREATED);
    }

    /**
     * Update existing Product
     * @param productId Product ID (in {@link java.lang.String} format)
     * @param reqBody {@link com.jseric.simple_product_rest.model.fe.CreateProductRequest}
     * @return {@link org.springframework.http.ResponseEntity}&lt;{@link com.jseric.simple_product_rest.model.fe.CreateProductResponse}&gt;
     */
    public ResponseEntity<CreateProductResponse> update(final String productId, final CreateProductRequest reqBody) {
        final CreateProductResponse rspBody = new CreateProductResponse();

        if (reqBody == null || reqBody.getProduct() == null) {
            log.error("Request body is empty!");
            rspBody.setErrorMessage("request body is empty;");
            return new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST);
        }
        final ProductWrapper requestData = reqBody.getProduct();

        if (StringUtils.isEmpty(productId)) {
            log.debug("productId is empty");
            rspBody.setErrorMessage("productId is empty;");
            return new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND);
        }

        Long id = null;
        try {
            id = Long.parseLong(productId);
        } catch (final NumberFormatException e) {
            log.error("productId is not a number!");
            rspBody.setErrorMessage("invalid productId;");
            return new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND);
        }

        // Validate data
        log.info("Validating Product data");
        final String errorMessage = productValidationService.validateCreateUpdateRequest(requestData);
        if (!errorMessage.isEmpty()) {
            log.info("Data validation failed. Errors exist.");
            log.trace("errorMessage :: " + errorMessage);
            rspBody.setErrorMessage(errorMessage);
            return new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST);
        }

        // Check that code is unique (i.e. it doesn't already exist in system)
        log.info("Validating code uniqueness");
        if (productRepository.doesExistByCode(requestData.getCode(), id)) {
            log.info("Code is not unique. Another object with same code field was found");
            rspBody.setErrorMessage("another object with same code field already exists in system;");
            return new ResponseEntity<>(rspBody, HttpStatus.CONFLICT);
        }

        // Fetch price in EUR currency
        log.info("Converting price in HRK to EUR");
        final BigDecimal priceEur = currencyConversionService.convertHrkToEur(requestData.getPriceHrk());

        // Fetch product
        final Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            log.warn("Product with ID = " + id + " not found");
            rspBody.setErrorMessage("product was not found;");
            return new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND);
        }

        // Update product data
        Product product = productOptional.get();
        product.setCode(requestData.getCode())
                .setName(requestData.getName())
                .setPriceHrk(requestData.getPriceHrk())
                .setPriceEur(priceEur)
                .setDescription(requestData.getDescription())
                .setIsAvailable(requestData.getIsAvailable());

        // Save product
        log.info("Saving updated product");
        log.debug(product.toString());
        product = productRepository.save(product);

        rspBody.setProduct(new ProductWrapper(product));
        return new ResponseEntity<>(rspBody, HttpStatus.OK);
    }

    /**
     * Delete Product
     * @param productId Product ID (in {@link java.lang.String} format)
     * @return {@link org.springframework.http.ResponseEntity}&lt;&gt;
     */
    public ResponseEntity<Object> delete(final String productId) {
        if (StringUtils.isEmpty(productId)) {
            log.debug("productId is empty");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Long id = null;
        try {
            id = Long.parseLong(productId);
        } catch (final NumberFormatException e) {
            log.error("productId is not a number!");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch product
        final Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            log.info("Product with ID not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Delete product
        log.info("Deleting product");
        productRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Fetch all Products
     * @return {@link org.springframework.http.ResponseEntity}&lt;{@link com.jseric.simple_product_rest.model.fe.FetchProductResponse}&gt;
     */
    public ResponseEntity<FetchProductResponse> fetchAll() {
        final FetchProductResponse rspBody = new FetchProductResponse();

        // Fetch products
        log.info("Fetching all products");
        final List<Product> products = productRepository.findAll();

        // Convert Product list to ProductWrapper list
        rspBody.setProducts(products.stream().map(ProductWrapper::new).collect(Collectors.toList()));

        return new ResponseEntity<>(rspBody, HttpStatus.OK);
    }

    /**
     * Fetch Product by ID
     * @param productId Product ID (in {@link java.lang.String} format)
     * @return {@link org.springframework.http.ResponseEntity}&lt;{@link com.jseric.simple_product_rest.model.fe.FetchProductResponse}&gt;
     */
    public ResponseEntity<FetchProductResponse> fetchById(final String productId) {
        final FetchProductResponse rspBody = new FetchProductResponse();

        if (StringUtils.isEmpty(productId)) {
            log.debug("productId is empty");
            rspBody.setErrorMessage("productId is empty;");
            return new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND);
        }

        Long id = null;
        try {
            id = Long.parseLong(productId);
        } catch (final NumberFormatException e) {
            log.error("productId is not a number!");
            return new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND);
        }

        // Fetch product
        final Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            log.debug("Product with ID not found");
            return new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND);
        }

        rspBody.setProducts(Arrays.asList(new ProductWrapper(productOptional.get())));

        return new ResponseEntity<>(rspBody, HttpStatus.OK);
    }
}
