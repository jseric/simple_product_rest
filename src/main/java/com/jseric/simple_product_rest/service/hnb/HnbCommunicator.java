package com.jseric.simple_product_rest.service.hnb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HnbCommunicator {
    @Value("${com.jseric.simple_product_rest.hnb.uri}")
    private String HNB_API_URI;

    private static final String CURRENCY_CODE_PARAM = "valuta";
    private static final String DATE_PARAM = "datum";
    private static final String EUR_CC = "EUR";

    private static final String AVG_CONVERSION_RATE_NODE = "Srednji za devize";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public HnbCommunicator() {
    }

    /**
     * Fetches today's HRK to EUR conversion rate from HNB API.
     * @return Average conversion rate. null will be returned in case of any error.
     */
    public BigDecimal fetchTodaysEuroConversionRate() {
        return fetchConversionRate(EUR_CC, null);
    }

    /**
     * Send GET request to HNB API (https://api.hnb.hr) to get average conversion rate for desired currencyCode and date
     * @param currencyCode Currency code
     * @param date Date (null for today)
     * @return Average conversion rate. null will be returned in case of any error.
     */
    private BigDecimal fetchConversionRate(final String currencyCode, final Date date) {
        final StringBuilder uri = new StringBuilder();
        uri.append(HNB_API_URI).append(CURRENCY_CODE_PARAM).append('=').append(currencyCode);
        if (date != null) {
            uri.append('&').append(DATE_PARAM).append('=').append(DATE_FORMAT.format(date));
        }

        log.info("Sending a GET request to " + uri);
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> response = restTemplate.getForEntity(uri.toString(), String.class);
        return parseReponseObject(response);
    }

    /**
     * Extract average conversion rate from response object.
     *
     * @param response {@link org.springframework.http.ResponseEntity<String>} object
     * @return Average conversion rate from response body.
     *          If response status is not HTTP OK or if response body is not JSON or
     *          if average conversion rate field is not in decimal format, null will be returned.
     */
    private BigDecimal parseReponseObject(ResponseEntity<String> response) {
        log.trace("Response :: " + response);

        // Check status code
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            // Something went wrong
            log.info("Response code is " + response.getStatusCode());
            return null;
        }

        String avgConversionRate = null;
        try {
            // Parse response body as JSON
            final JSONArray jsonArray = new JSONArray(response.getBody());
            avgConversionRate = jsonArray.getJSONObject(0).getString(AVG_CONVERSION_RATE_NODE);
        } catch (final JSONException e) {
            log.warn("There was a problem parsing response JSON: " + e.getMessage());
            return null;
        }

        try {
            return new BigDecimal(avgConversionRate.replaceAll(",", "."));
        } catch (final NumberFormatException e) {
            log.warn("Response body field for avg conversion rate does not contain a decimal number");
            return null;
        }
    }

}
