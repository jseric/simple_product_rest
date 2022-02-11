package com.jseric.simple_product_rest.service.hnb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CurrencyConversionService {
    private static int INVALID_VALUE = 0;

    private final HnbCommunicator hnbCommunicator;

    @Autowired
    public CurrencyConversionService(HnbCommunicator hnbCommunicator) {
        this.hnbCommunicator = hnbCommunicator;
    }

    /**
     * Convert value in HRK currency to price in EUR currency.
     * Conversion rate is retrieved from HNB API.
     * @param hrk Value in HRK currency
     * @return Converted rate.
     *         If HRK value is null, null is returned
     *         If conversion rate cannot be fetched, null is returned.
     */
    public BigDecimal convertHrkToEur(final BigDecimal hrk) {
        if (hrk == null) {
            return null;
        }

        final BigDecimal conversionRate = hnbCommunicator.fetchTodaysEuroConversionRate();
        if (conversionRate == null) {
            // Conversion rate could not be fetched, return invalid value
            return new BigDecimal(INVALID_VALUE);
        }
        return hrk.divide(conversionRate, 2, RoundingMode.HALF_EVEN);
    }
}
