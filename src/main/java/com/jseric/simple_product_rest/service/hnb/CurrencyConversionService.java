package com.jseric.simple_product_rest.service.hnb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CurrencyConversionService {
    private static final BigDecimal INVALID_VALUE = BigDecimal.ZERO;

    private final HnbCommunicator hnbCommunicator;

    @Autowired
    public CurrencyConversionService(HnbCommunicator hnbCommunicator) {
        this.hnbCommunicator = hnbCommunicator;
    }

    public CurrencyConversionService() {
        hnbCommunicator = new HnbCommunicator();
    }

    /**
     * Convert value in HRK currency to price in EUR currency.
     * Conversion rate is retrieved from HNB API.
     * @param hrk Value in HRK currency
     * @return Converted rate.
     *         If HRK value is null, {@link com.jseric.simple_product_rest.service.hnb.CurrencyConversionService#INVALID_VALUE} is returned.
     *         If conversion rate cannot be fetched, {@link com.jseric.simple_product_rest.service.hnb.CurrencyConversionService#INVALID_VALUE} is returned.
     */
    public BigDecimal convertHrkToEur(final BigDecimal hrk) {
        if (hrk == null) {
            return INVALID_VALUE;
        }

        if (hrk.compareTo(BigDecimal.ZERO) == 0) {
            // HRK value is 0.00, so 0.00 can be returned without the need for fetching the conversion rate
            return BigDecimal.ZERO;
        }

        final BigDecimal conversionRate = hnbCommunicator.fetchTodaysEuroConversionRate();
        if (conversionRate == null) {
            // Conversion rate could not be fetched, return invalid value
            return INVALID_VALUE;
        }
        return hrk.divide(conversionRate, 2, RoundingMode.HALF_EVEN);
    }
}
