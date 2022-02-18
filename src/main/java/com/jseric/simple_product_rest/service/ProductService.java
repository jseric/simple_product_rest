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
import org.springframework.beans.factory.annotation.Autowired;
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
     * @return {@link com.jseric.simple_product_rest.model.fe.CreateProductResponse}
     */
    public CreateProductResponse createNewProduct(final CreateProductRequest reqBody) {
        final CreateProductResponse rspBody = new CreateProductResponse();

        if (reqBody == null || reqBody.getProduct() == null) {
            log.error("Request body is empty!");
            rspBody.setErrorMessage("request body is empty");
            return rspBody;
        }

        final ProductWrapper requestData = reqBody.getProduct();

        // Validate data
        log.info("Validating new Product data");
        final String errorMessage = productValidationService.validateCreateNewProductRequest(requestData);
        if (!errorMessage.isEmpty()) {
            log.info("Data validation failed. Errors exist.");
            log.trace("errorMessage :: " + errorMessage);
            rspBody.setErrorMessage(errorMessage);
            return rspBody;
        }

        // Check that code is unique (i.e. it doesn't already exist in system)
        log.info("Validating code uniqueness");
        if (productRepository.doesExistByCode(requestData.getCode())) {
            log.info("Code is not unique. Another object with same code field was found");
            rspBody.setErrorMessage("another object with same code field already exists in system;");
            return rspBody;
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
        try {
            product = productRepository.save(product);
        } catch (final Exception e) {
            log.error("Error occurred while saving product: " + e.getMessage());
            rspBody.setErrorMessage("product was not saved");
            return rspBody;
        }

        rspBody.setProduct(new ProductWrapper(product));
        return rspBody;
    }

    /**
     * Update existing Product
     * @param productId Product ID (in {@link java.lang.String} format)
     * @param reqBody {@link com.jseric.simple_product_rest.model.fe.CreateProductRequest}
     * @return {@link com.jseric.simple_product_rest.model.fe.CreateProductResponse}
     */
    public CreateProductResponse updateProduct(final String productId, final CreateProductRequest reqBody) {
        final CreateProductResponse rspBody = new CreateProductResponse();

        if (reqBody == null || reqBody.getProduct() == null) {
            log.error("Request body is empty!");
            rspBody.setErrorMessage("request body is empty");
            return rspBody;
        }
        final ProductWrapper requestData = reqBody.getProduct();

        Long id = null;
        try {
            id = Long.parseLong(productId);
        } catch (final NumberFormatException e) {
            log.error("productId is not a number!");
            rspBody.setErrorMessage("invalid productId");
            return rspBody;
        }

        // Validate data
        log.info("Validating Product data");
        final String errorMessage = productValidationService.validateCreateNewProductRequest(requestData);
        if (!errorMessage.isEmpty()) {
            log.info("Data validation failed. Errors exist.");
            log.trace("errorMessage :: " + errorMessage);
            rspBody.setErrorMessage(errorMessage);
            return rspBody;
        }

        // Check that code is unique (i.e. it doesn't already exist in system)
        log.info("Validating code uniqueness");
        if (productRepository.doesExistByCode(requestData.getCode(), id)) {
            log.info("Code is not unique. Another object with same code field was found");
            rspBody.setErrorMessage("another object with same code field already exists in system;");
            return rspBody;
        }

        // Fetch price in EUR currency
        log.info("Converting price in HRK to EUR");
        final BigDecimal priceEur = currencyConversionService.convertHrkToEur(requestData.getPriceHrk());

        // Fetch product
        final Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            log.warn("Product with ID not found");
            rspBody.setErrorMessage("product was not found");
            return rspBody;
        }

        // Update product data
        Product product = productOptional.get();
        product.setCode(requestData.getCode());
        product.setName(requestData.getName());
        product.setPriceHrk(requestData.getPriceHrk());
        product.setPriceEur(priceEur);
        product.setDescription(requestData.getDescription());
        product.setIsAvailable(requestData.getIsAvailable());


        // Save product
        log.info("Saving updated product");
        try {
            product = productRepository.save(product);
        } catch (final Exception e) {
            log.error("Error ocurred while updating product: " + e.getMessage());
            rspBody.setErrorMessage("updated product was not saved");
            return rspBody;
        }

        rspBody.setProduct(new ProductWrapper(product));
        return rspBody;
    }

    /**
     * Delete Product
     * @param productId Product ID (in {@link java.lang.String} format)
     * @return Action result
     */
    public boolean deleteProduct(final String productId) {
        Long id = null;
        try {
            id = Long.parseLong(productId);
        } catch (final NumberFormatException e) {
            log.error("productId is not a number!");
            return false;
        }

        // Fetch product
        final Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            log.info("Product with ID not found");
            return false;
        }

        // Delete product
        log.info("Deleting product");
        try {
            productRepository.deleteById(id);
        } catch (final Exception e) {
            log.error("Error occurred while updating product: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Fetch all Products
     * @return {@link com.jseric.simple_product_rest.model.fe.FetchProductResponse}
     */
    public FetchProductResponse fetchAllProducts() {
        final FetchProductResponse rspBody = new FetchProductResponse();

        // Fetch products
        log.info("Fetching all products");
        List<Product> products = null;
        try {
            products = productRepository.findAll();
        } catch (final Exception e) {
            log.error("Error while fetching products: " + e.getMessage());
            rspBody.setErrorMessage("Error fetching products");
            return rspBody;
        }

        // Convert Product list to ProductWrapper list
        rspBody.setProducts(products.stream().map(ProductWrapper::new).collect(Collectors.toList()));

        return rspBody;
    }

    /**
     * Fetch Product by ID
     * @param productId Product ID (in {@link java.lang.String} format)
     * @return {@link com.jseric.simple_product_rest.model.fe.FetchProductResponse}
     */
    public FetchProductResponse fetchProductById(final String productId) {
        final FetchProductResponse rspBody = new FetchProductResponse();

        Long id = null;
        try {
            id = Long.parseLong(productId);
        } catch (final NumberFormatException e) {
            return rspBody;
        }

        // Fetch product
        final Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            log.debug("Product with ID not found");
            return rspBody;
        }

        rspBody.setProducts(Arrays.asList(new ProductWrapper(productOptional.get())));

        return rspBody;
    }
}
