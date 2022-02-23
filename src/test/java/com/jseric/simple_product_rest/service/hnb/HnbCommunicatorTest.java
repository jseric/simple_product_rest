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
class HnbCommunicatorTest {
    @Autowired
    HnbCommunicator hnbCommunicator;

    @Test
    void fetchTodaysEuroConversionRate_successfulFetch() {
        stubFor(get("/tecajn/v1?valuta=EUR").willReturn(aResponse()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("[{\"Broj tečajnice\":\"37\",\"Datum primjene\":\"01.01.2022\",\"Država\":\"EMU\",\"Šifra " +
                                  "valute\":\"978\",\"Valuta\":\"EUR\",\"Jedinica\":1,\"Kupovni za devize\":\"7,500000\"," +
                                  "\"Srednji za devize\":\"7,500000\",\"7,500000\":\"7,500000\"}]")));

        assertThat(hnbCommunicator.fetchTodaysEuroConversionRate()).isEqualTo(new BigDecimal(7.500000).setScale(6));
    }

    @Test
    void fetchTodaysEuroConversionRate_invalidCurrency() {
        stubFor(get("/tecajn/v1?valuta=EUR").willReturn(aResponse()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("[]")));

        assertThat(hnbCommunicator.fetchTodaysEuroConversionRate()).isNull();
    }

    @Test
    void fetchTodaysEuroConversionRate_serviceIsDown() {
        assertThat(hnbCommunicator.fetchTodaysEuroConversionRate()).isNull();
    }
}
