package com.jseric.simple_product_rest.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.jseric.simple_product_rest.model.fe.CreateProductRequest;
import com.jseric.simple_product_rest.model.fe.CreateProductResponse;
import com.jseric.simple_product_rest.model.fe.FetchProductResponse;
import com.jseric.simple_product_rest.model.fe.ProductWrapper;
import com.jseric.simple_product_rest.model.product.Product;
import com.jseric.simple_product_rest.repository.ProductRepository;
import com.jseric.simple_product_rest.service.hnb.CurrencyConversionService;
import com.jseric.simple_product_rest.service.validation.ProductValidationService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@WireMockTest(httpPort = 8090)
class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Autowired
    ProductValidationService productValidationService;

    @Autowired
    CurrencyConversionService currencyConversionService;

    ProductService productService;

    private final static Long EXISTING_ID1 = 1L;
    private final static String VALID_CODE1 = "1234567890";
    private final static String VALID_NAME1 = "Test name 123";
    private final static BigDecimal VALID_PRICE_HRK1 = new BigDecimal(75.00).setScale(2);
    private final static BigDecimal VALID_PRICE_EUR1 = new BigDecimal(10.00).setScale(2);
    private final static String VALID_DESCRIPTION1 = "Test description 123";
    private final static Boolean VALID_IS_AVAILABLE1 = true;

    private final static Long EXISTING_ID2 = 2L;
    private final static String VALID_CODE2 = "1234567891";
    private final static String VALID_NAME2 = "Test name 321";
    private final static BigDecimal VALID_PRICE_HRK2 = new BigDecimal(150.00).setScale(2);
    private final static BigDecimal VALID_PRICE_EUR2 = new BigDecimal(20.00).setScale(2);
    private final static String VALID_DESCRIPTION2 = "Test description 321";
    private final static Boolean VALID_IS_AVAILABLE2 = true;

    private final static Long NONEXISTING_ID = 3L;
    private final static String VALID_CODE3 = "1234567892";
    private final static String VALID_NAME3 = "Test name 213";
    private final static BigDecimal VALID_PRICE_HRK3 = new BigDecimal(300.00).setScale(2);
    private final static BigDecimal VALID_PRICE_EUR3 = new BigDecimal(40.00).setScale(2);
    private final static String VALID_DESCRIPTION3 = "Test description 213";
    private final static Boolean VALID_IS_AVAILABLE3 = true;

    private final static String INVALID_ID = "abc";
    private final static String INVALID_CODE = "123";
    private final static BigDecimal INVALID_PRICE_HRK = new BigDecimal(-150.00);

    private final static String ERR_PRODUCT_ID_EMPTY = "productId is empty;";
    private final static String ERR_INVALID_PRODUCT_ID = "invalid productId;";
    private final static String ERR_REQUEST_BODY_EMPTY = "request body is empty;";
    private final static String ERR_CREATE_UPDATE_PRODUCT_DATA_EMPTY = "code field is empty; name field is empty; priceHrk field is " +
            "empty; isAvailable field is empty;";
    private final static String ERR_CREATE_UPDATE_PRICE_HRK_EMPTY = "priceHrk field is empty;";
    private final static String ERR_CREATE_UPDATE_INVALID_CODE_PRICE_HRK = "code field is not of length 10; priceHrk field must" +
            " be greater or equal to 0;";
    private final static String ERR_CREATE_UPDATE_CODE_NOT_UNIQUE = "another object with same code field already exists in system;";
    private final static String ERR_PRODUCT_NOT_FOUND = "product was not found;";

    @BeforeEach
    void setUpBeforeEach() {
        productService = new ProductService(productRepository, productValidationService, currencyConversionService);

        when(productRepository.findById(EXISTING_ID1)).thenReturn(Optional.of(createValidProduct(EXISTING_ID1, true)));
        when(productRepository.findById(EXISTING_ID2)).thenReturn(Optional.of(createValidProduct(EXISTING_ID2, true)));
        when(productRepository.findById(NONEXISTING_ID)).thenReturn(Optional.empty());

        when(productRepository.doesExistByCode(VALID_CODE1, EXISTING_ID1)).thenReturn(false);
        when(productRepository.doesExistByCode(VALID_CODE1, EXISTING_ID2)).thenReturn(true);
        when(productRepository.doesExistByCode(VALID_CODE1, NONEXISTING_ID)).thenReturn(true);
        when(productRepository.doesExistByCode(VALID_CODE2, EXISTING_ID1)).thenReturn(true);
        when(productRepository.doesExistByCode(VALID_CODE2, EXISTING_ID2)).thenReturn(false);
        when(productRepository.doesExistByCode(VALID_CODE2, NONEXISTING_ID)).thenReturn(true);
        when(productRepository.doesExistByCode(VALID_CODE3, EXISTING_ID1)).thenReturn(false);
        when(productRepository.doesExistByCode(VALID_CODE3, EXISTING_ID2)).thenReturn(false);
        when(productRepository.doesExistByCode(VALID_CODE3, NONEXISTING_ID)).thenReturn(false);

        when(productRepository.doesExistByCode(VALID_CODE1)).thenReturn(true);
        when(productRepository.doesExistByCode(VALID_CODE2)).thenReturn(true);
        when(productRepository.doesExistByCode(VALID_CODE3)).thenReturn(false);

        stubFor(get("/tecajn/v1?valuta=EUR").willReturn(aResponse()
                    .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody("[{\"Broj tečajnice\":\"37\",\"Datum primjene\":\"01.01.2022\",\"Država\":\"EMU\",\"Šifra " +
                                      "valute\":\"978\",\"Valuta\":\"EUR\",\"Jedinica\":1,\"Kupovni za devize\":\"7,500000\"," +
                                      "\"Srednji za devize\":\"7,500000\",\"7,500000\":\"7,500000\"}]")));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createAndSave_nullOrEmptyBody() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // body or product is null
        rspBody.setErrorMessage(ERR_REQUEST_BODY_EMPTY);
        assertThat(productService.createAndSave(null)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));
        assertThat(productService.createAndSave(reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));

        // product (in body) is empty
        reqBody.setProduct(new ProductWrapper());
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_PRODUCT_DATA_EMPTY);
        assertThat(productService.createAndSave(reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));
    }

    @Test
    void createAndSave_invalidBody() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // product is missing some data (priceHrk)
        reqBody.setProduct(
                new ProductWrapper(createValidProduct(null, false).setPriceHrk(null)));
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_PRICE_HRK_EMPTY);
        assertThat(productService.createAndSave(reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));

        // product has invalid data (code, priceHrk)
        reqBody.setProduct(
                new ProductWrapper(createValidProduct(null, false).setCode(INVALID_CODE).setPriceHrk(INVALID_PRICE_HRK)));
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_INVALID_CODE_PRICE_HRK);
        assertThat(productService.createAndSave(reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));
    }

    @Test
    void createAndSave_conflictingCode() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // Code exists for another ID
        reqBody.setProduct(new ProductWrapper(createValidProduct(EXISTING_ID1, false)));
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_CODE_NOT_UNIQUE);
        assertThat(productService.createAndSave(reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.CONFLICT));
    }

    @Test
    void createAndSave_success() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        Product preSavedProduct = createValidProduct(null, false);
        Product postSavedProduct = createValidProduct(null, true);

        when(productRepository.save(any(Product.class))).thenReturn(postSavedProduct);
        reqBody.setProduct(new ProductWrapper(preSavedProduct));
        rspBody.setProduct(new ProductWrapper(postSavedProduct));
        assertThat(productService.createAndSave(reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.CREATED));
    }

    @Test
    void update_nullOrEmptyBody() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // body or product is null
        rspBody.setErrorMessage(ERR_REQUEST_BODY_EMPTY);
        assertThat(productService.update(String.valueOf(EXISTING_ID1), null)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));
        assertThat(productService.update(String.valueOf(EXISTING_ID1), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));

        // product (in body) is empty
        reqBody.setProduct(new ProductWrapper());
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_PRODUCT_DATA_EMPTY);
        assertThat(productService.update(String.valueOf(EXISTING_ID1), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));
    }

    @Test
    void update_invalidBody() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // product is missing some data (priceHrk)
        reqBody.setProduct(
                new ProductWrapper(createValidProduct(EXISTING_ID1, false).setPriceHrk(null)));
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_PRICE_HRK_EMPTY);
        assertThat(productService.update(String.valueOf(EXISTING_ID1), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));

        // product has invalid data (code, priceHrk)
        reqBody.setProduct(
                new ProductWrapper(createValidProduct(EXISTING_ID1, false).setCode(INVALID_CODE).setPriceHrk(INVALID_PRICE_HRK)));
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_INVALID_CODE_PRICE_HRK);
        assertThat(productService.update(String.valueOf(EXISTING_ID1), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.BAD_REQUEST));
    }

    @Test
    void update_invalidProductId() {
        CreateProductRequest reqBody = new CreateProductRequest();
        reqBody.setProduct(new ProductWrapper(createValidProduct(EXISTING_ID1, false)));
        CreateProductResponse rspBody = new CreateProductResponse();

        // productId is null or empty string
        rspBody.setErrorMessage(ERR_PRODUCT_ID_EMPTY);
        assertThat(productService.update(null, reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));
        assertThat(productService.update("", reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));

        // productId is invalid
        rspBody.setErrorMessage(ERR_INVALID_PRODUCT_ID);
        assertThat(productService.update(INVALID_ID, reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));
    }

    @Test
    void update_conflictingCode() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // Code exists for another ID
        reqBody.setProduct(new ProductWrapper(createValidProduct(EXISTING_ID1, false).setCode(VALID_CODE2)));
        rspBody.setErrorMessage(ERR_CREATE_UPDATE_CODE_NOT_UNIQUE);
        assertThat(productService.update(String.valueOf(EXISTING_ID1), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.CONFLICT));
    }

    @Test
    void update_nonExistingId() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // Product with ID not found
        reqBody.setProduct(new ProductWrapper(createValidProduct(null, false)));
        rspBody.setErrorMessage(ERR_PRODUCT_NOT_FOUND);
        assertThat(productService.update(String.valueOf(NONEXISTING_ID), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));
    }

    @Test
    void update_success() {
        CreateProductRequest reqBody = new CreateProductRequest();
        CreateProductResponse rspBody = new CreateProductResponse();

        // Updated (with no changes)
        Product preSavedProduct = createValidProduct(EXISTING_ID1, false);
        Product postSavedProduct = createValidProduct(EXISTING_ID1, true);
        when(productRepository.save(any(Product.class))).thenReturn(postSavedProduct);
        reqBody.setProduct(new ProductWrapper(preSavedProduct));
        rspBody.setProduct(new ProductWrapper(postSavedProduct));
        assertThat(productService.update(String.valueOf(EXISTING_ID1), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.OK));

        // Updated (with changes to code)
        preSavedProduct = createValidProduct(EXISTING_ID1, true).setCode(VALID_CODE3);
        postSavedProduct = createValidProduct(EXISTING_ID1, true).setCode(VALID_CODE3);
        when(productRepository.save(any(Product.class))).thenReturn(postSavedProduct);
        reqBody.setProduct(new ProductWrapper(createValidProduct(EXISTING_ID1, false).setCode(VALID_CODE3)));
        rspBody.setProduct(new ProductWrapper(createValidProduct(EXISTING_ID1, true).setCode(VALID_CODE3)));
        assertThat(productService.update(String.valueOf(EXISTING_ID1), reqBody)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.OK));
    }

    @Test
    void delete() {
        doNothing().when(productRepository).deleteById(EXISTING_ID1);
        doThrow(new IllegalArgumentException()).when(productRepository).deleteById(null);

        assertThat(productService.delete("")).isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        assertThat(productService.delete(null)).isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        assertThat(productService.delete(INVALID_ID)).isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        assertThat(productService.delete(String.valueOf(NONEXISTING_ID))).isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        assertThat(productService.delete(String.valueOf(EXISTING_ID1))).isEqualTo(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @Test
    void fetchAll() {
        // DB has data
        final List<Product> existingProducts = Arrays.asList(createValidProduct(EXISTING_ID1, true), createValidProduct(EXISTING_ID2, true));
        when(productRepository.findAll()).thenReturn(existingProducts);
        FetchProductResponse rspBody = new FetchProductResponse();
        rspBody.setProducts(existingProducts.stream().map(ProductWrapper::new).collect(Collectors.toList()));
        assertThat(productService.fetchAll()).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.OK));

        // DB is empty
        when(productRepository.findAll()).thenReturn(Collections.EMPTY_LIST);
        rspBody = new FetchProductResponse();
        assertThat(productService.fetchAll()).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.OK));
    }

    @Test
    void fetchById() {
        FetchProductResponse rspBody = new FetchProductResponse();
        rspBody.setProducts(Arrays.asList(new ProductWrapper(createValidProduct(EXISTING_ID1, true))));
        assertThat(productService.fetchById(String.valueOf(EXISTING_ID1))).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.OK));

        rspBody = new FetchProductResponse();
        assertThat(productService.fetchById(String.valueOf(NONEXISTING_ID))).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));
        assertThat(productService.fetchById(INVALID_ID)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));

        rspBody.setErrorMessage(ERR_PRODUCT_ID_EMPTY);
        assertThat(productService.fetchById("")).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));
        assertThat(productService.fetchById(null)).isEqualTo(new ResponseEntity<>(rspBody, HttpStatus.NOT_FOUND));
    }

    private Product createValidProduct(final Long id, final boolean fillAllFields) {
        final LocalDateTime ts = LocalDateTime.of(2022, 1, 1, 0, 0);
        Product product = new Product();

        if (id == null) {
            if (fillAllFields) {
                product.setId(NONEXISTING_ID).setCreatedTimestamp(ts).setUpdatedTimestamp(ts);
                product.setPriceEur(VALID_PRICE_EUR3);
            }
            product.setCode(VALID_CODE3).setName(VALID_NAME3).setPriceHrk(VALID_PRICE_HRK3).setPriceEur(VALID_PRICE_EUR3);
            product.setDescription(VALID_DESCRIPTION3).setIsAvailable(VALID_IS_AVAILABLE3);
        } else if (id.equals(EXISTING_ID1)) {
            if (fillAllFields) {
                product.setId(EXISTING_ID1).setCreatedTimestamp(ts).setUpdatedTimestamp(ts);
                product.setPriceEur(VALID_PRICE_EUR1);
            }
            product.setCode(VALID_CODE1).setName(VALID_NAME1).setPriceHrk(VALID_PRICE_HRK1).setPriceEur(VALID_PRICE_EUR1);
            product.setDescription(VALID_DESCRIPTION1).setIsAvailable(VALID_IS_AVAILABLE1);
        } else if (id.equals(EXISTING_ID2)) {
            if (fillAllFields) {
                product.setId(EXISTING_ID2).setCreatedTimestamp(ts).setUpdatedTimestamp(ts);
                product.setPriceEur(VALID_PRICE_EUR2);
            }
            product.setCode(VALID_CODE2).setName(VALID_NAME2).setPriceHrk(VALID_PRICE_HRK2).setPriceEur(VALID_PRICE_EUR2);
            product.setDescription(VALID_DESCRIPTION2).setIsAvailable(VALID_IS_AVAILABLE2);
        }

        return product;
    }
}
