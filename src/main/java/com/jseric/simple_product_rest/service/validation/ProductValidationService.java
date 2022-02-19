package com.jseric.simple_product_rest.service.validation;

import com.jseric.simple_product_rest.model.fe.ProductWrapper;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductValidationService {
    private static final int PRODUCT_CODE_LENGTH = 10;

    @Autowired
    public ProductValidationService() {
    }

    /**
     * Validate body of create new and update existing product request
     * @param requestBody {@link com.jseric.simple_product_rest.model.fe.ProductWrapper} object
     * @return String containing errors (if they exist). Empty string if no errors exist.
     * @throws NullPointerException if requestBody is null
     */
    public String validateCreateUpdateRequest(final ProductWrapper requestBody) throws NullPointerException {
        if (requestBody == null) {
            throw new NullPointerException("Request body is null");
        }

        final StringBuilder errors = new StringBuilder();

        // Validate code
        log.debug("Validating code field");
        final String code = requestBody.getCode();
        if (StringUtils.isEmpty(code)) {
            log.info("code field is empty");
            errors.append("code field is empty; ");
        } else if (code.length() != PRODUCT_CODE_LENGTH) {
            log.info("code field has invalid length");
            errors.append("code field is not of length ").append(PRODUCT_CODE_LENGTH).append("; ");
        }

        // Validate name
        log.debug("Validating name field");
        if (StringUtils.isEmpty(requestBody.getName())) {
            log.info("name field is empty");
            errors.append("name field is empty; ");
        }

        // Validate priceHrk
        log.debug("Validating priceHrk field");
        final BigDecimal priceHrk = requestBody.getPriceHrk();
        if (priceHrk == null) {
            log.info("priceHrk field is empty");
            errors.append("priceHrk field is empty; ");
        } else if (priceHrk.compareTo(BigDecimal.ZERO) < 0) {
            log.info("priceHrk field content is lower than zero");
            errors.append("priceHrk field must be greater or equal to 0; ");
        }

        // Validate isAvailable
        log.debug("Validating isAvailable field");
        if (requestBody.getIsAvailable() == null) {
            log.info("isAvailable field is empty");
            errors.append("isAvailable field is empty; ");
        }

        if (errors.length() != 0) {
            // Strip last space in errors SB
            errors.setLength(errors.length() - 1);
        }

        return errors.toString();
    }
}
