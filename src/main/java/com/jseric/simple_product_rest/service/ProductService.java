package com.jseric.simple_product_rest.service;

import com.jseric.simple_product_rest.model.fe.CreateProductRequest;
import com.jseric.simple_product_rest.model.fe.CreateProductResponse;
import com.jseric.simple_product_rest.model.fe.ProductWrapper;
import com.jseric.simple_product_rest.model.product.Product;
import com.jseric.simple_product_rest.repository.ProductRepository;
import com.jseric.simple_product_rest.service.hnb.CurrencyConversionService;
import com.jseric.simple_product_rest.service.validation.ProductValidationService;
import java.math.BigDecimal;
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
     * @param reqBody {@link com.jseric.simple_product_rest.model.fe.CreateProductResponse}
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
        if (productRepository.countByCode(requestData.getCode()) != 0) {
            log.info("Code is not unique. Another object with same code field was found");
            rspBody.setErrorMessage("another object with same code field already exists in system;");
            return rspBody;
        }

        // Fetch price in EUR currency
        log.info("Converting price in HRK to EUR");
        BigDecimal priceEur = currencyConversionService.convertHrkToEur(requestData.getPriceHrk());
        if (priceEur.intValue() == currencyConversionService.INVALID_VALUE) {
            // FIXME should product with eur = -1 be saved or dumped?
        }

        // Create new Product
        Product product = new Product(
                requestData.getCode(),
                requestData.getName(),
                requestData.getPriceHrk(),
                priceEur,
                requestData.getDescription(),
                requestData.getIsAvailable());

        // Save product
        try {
            log.info("Saving new product");
            product = productRepository.save(product);
        } catch (final Exception e) {
            log.error("Error ocurred while saving product: " + e.getMessage());
            rspBody.setErrorMessage("Product was not saved");
            return rspBody;
        }

        rspBody.setProduct(new ProductWrapper(product));
        return rspBody;
    }
}
