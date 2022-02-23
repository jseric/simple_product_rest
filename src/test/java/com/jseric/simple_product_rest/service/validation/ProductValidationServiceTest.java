package com.jseric.simple_product_rest.service.validation;

import com.jseric.simple_product_rest.model.fe.ProductWrapper;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductValidationServiceTest {
    private static final int PRODUCT_CODE_LENGTH = 10;

    private final static String CODE_MISSING_ERR = "code field is empty; ";
    private final static String INVALID_CODE_LENGTH_ERR = "code field is not of length " + PRODUCT_CODE_LENGTH + "; ";
    private final static String NAME_MISSING_ERR = "name field is empty; ";
    private final static String PRICE_HRK_MISSING_ERR = "priceHrk field is empty; ";
    private final static String INVALID_PRICE_HRK_ERR = "priceHrk field must be greater or equal to 0; ";
    private final static String IS_AVAILABLE_MISSING_ERR = "isAvailable field is empty; ";

    private final static String INVALID_CODE = "123456";
    private final static String VALID_CODE = "1234567890";
    private final static String VALID_NAME = "Test name 123";
    private final static BigDecimal VALID_PRICE_HRK_1 = new BigDecimal(100.00);
    private final static BigDecimal VALID_PRICE_HRK_2 = new BigDecimal(0.00);
    private final static BigDecimal INVALID_PRICE_HRK = new BigDecimal(-100.00);
    private final static String VALID_DESCRIPTION = "Test description 123";
    private final static Boolean VALID_IS_AVAILABLE = true;

    @Autowired
    ProductValidationService productValidationService;

    @Test
    public void contextLoads() throws Exception {
        assertThat(productValidationService).isNotNull();
    }

    @Test
    void validateCreateUpdateRequest_validRequests() {
        final ProductWrapper pw = new ProductWrapper();
        pw.setCode(VALID_CODE);
        pw.setName(VALID_NAME);
        pw.setPriceHrk(VALID_PRICE_HRK_1);
        pw.setIsAvailable(VALID_IS_AVAILABLE);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isNullOrEmpty();

        pw.setDescription(VALID_DESCRIPTION);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isNullOrEmpty();

        pw.setPriceHrk(VALID_PRICE_HRK_2);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isNullOrEmpty();
    }

     @Test
    void validateCreateUpdateRequest_nullPtrRequestData() {
         ProductWrapper pw = null;
         assertThrows(NullPointerException.class, () -> {
             productValidationService.validateCreateUpdateRequest(pw);
         });
    }

    @Test
    void validateCreateUpdateRequest_invalidRequests() {
        ProductWrapper pw = new ProductWrapper();
        String expectedError = StringUtils.chop(CODE_MISSING_ERR + NAME_MISSING_ERR + PRICE_HRK_MISSING_ERR + IS_AVAILABLE_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setCode(INVALID_CODE);
        expectedError = StringUtils.chop(INVALID_CODE_LENGTH_ERR + NAME_MISSING_ERR + PRICE_HRK_MISSING_ERR + IS_AVAILABLE_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setCode(VALID_CODE);
        expectedError = StringUtils.chop(NAME_MISSING_ERR + PRICE_HRK_MISSING_ERR + IS_AVAILABLE_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setName(VALID_NAME);
        expectedError = StringUtils.chop(PRICE_HRK_MISSING_ERR + IS_AVAILABLE_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setPriceHrk(INVALID_PRICE_HRK);
        expectedError = StringUtils.chop(INVALID_PRICE_HRK_ERR + IS_AVAILABLE_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setPriceHrk(VALID_PRICE_HRK_1);
        expectedError = StringUtils.chop(IS_AVAILABLE_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setPriceHrk(VALID_PRICE_HRK_2);
        expectedError = StringUtils.chop(IS_AVAILABLE_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        // Reset productWrapper
        pw = new ProductWrapper();

        pw.setIsAvailable(VALID_IS_AVAILABLE);
        expectedError = StringUtils.chop(CODE_MISSING_ERR + NAME_MISSING_ERR + PRICE_HRK_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setCode(INVALID_CODE);
        expectedError = StringUtils.chop(INVALID_CODE_LENGTH_ERR + NAME_MISSING_ERR + PRICE_HRK_MISSING_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setPriceHrk(INVALID_PRICE_HRK);
        expectedError = StringUtils.chop(INVALID_CODE_LENGTH_ERR + NAME_MISSING_ERR + INVALID_PRICE_HRK_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setName(VALID_NAME);
        expectedError = StringUtils.chop(INVALID_CODE_LENGTH_ERR + INVALID_PRICE_HRK_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setPriceHrk(VALID_PRICE_HRK_1);
        expectedError = StringUtils.chop(INVALID_CODE_LENGTH_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);

        pw.setPriceHrk(VALID_PRICE_HRK_2);
        expectedError = StringUtils.chop(INVALID_CODE_LENGTH_ERR);
        assertThat(productValidationService.validateCreateUpdateRequest(pw)).isEqualTo(expectedError);
    }
}
