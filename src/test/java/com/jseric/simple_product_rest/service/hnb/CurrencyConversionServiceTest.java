package com.jseric.simple_product_rest.service.hnb;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
@WireMockTest(httpPort = 8090)
class CurrencyConversionServiceTest {
    private static final BigDecimal NEGATIVE_HRK = new BigDecimal(-75.00);
    private static final BigDecimal POSITIVE_HRK = new BigDecimal(75.00);

    private static final BigDecimal NEGATIVE_EUR = new BigDecimal(-10.00).setScale(2);
    private static final BigDecimal POSITIVE_EUR = new BigDecimal(10.00).setScale(2);


    @Autowired
    CurrencyConversionService currencyConversionService;

    @Test
    void convertHrkToEur_withWorkingHnbApi() {
        stubFor(get("/tecajn/v1?valuta=EUR").willReturn(aResponse()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("[{\"Broj tečajnice\":\"37\",\"Datum primjene\":\"01.01.2022\",\"Država\":\"EMU\",\"Šifra " +
                                  "valute\":\"978\",\"Valuta\":\"EUR\",\"Jedinica\":1,\"Kupovni za devize\":\"7,500000\"," +
                                  "\"Srednji za devize\":\"7,500000\",\"7,500000\":\"7,500000\"}]")));

        assertThat(currencyConversionService.convertHrkToEur(null)).isEqualTo(BigDecimal.ZERO);
        assertThat(currencyConversionService.convertHrkToEur(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO);
        assertThat(currencyConversionService.convertHrkToEur(NEGATIVE_HRK)).isEqualTo(NEGATIVE_EUR);
        assertThat(currencyConversionService.convertHrkToEur(POSITIVE_HRK)).isEqualTo(POSITIVE_EUR);
    }

    @Test
    void convertHrkToEur_withNonWorkingHnbApi() {
        assertThat(currencyConversionService.convertHrkToEur(null)).isEqualTo(BigDecimal.ZERO);
        assertThat(currencyConversionService.convertHrkToEur(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO);
        assertThat(currencyConversionService.convertHrkToEur(NEGATIVE_HRK)).isEqualTo(BigDecimal.ZERO);
        assertThat(currencyConversionService.convertHrkToEur(POSITIVE_HRK)).isEqualTo(BigDecimal.ZERO);
    }
}
