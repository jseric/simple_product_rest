package com.jseric.simple_product_rest.service.hnb;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.jseric.simple_product_rest.mock.WireMockManager;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@WireMockTest(httpPort = 8090)
class HnbCommunicatorTest {
    @Autowired
    HnbCommunicator hnbCommunicator;

    @Test
    void fetchTodaysEuroConversionRate_successfulFetch() {
        WireMockManager.startHnbEurStub();
        assertThat(hnbCommunicator.fetchTodaysEuroConversionRate()).isEqualTo(new BigDecimal(7.500000).setScale(6));
        WireMockManager.stopHnbEurStub();
    }

    @Test
    void fetchTodaysEuroConversionRate_invalidCurrency() {
        WireMockManager.startHnbInvalidStub();
        assertThat(hnbCommunicator.fetchTodaysEuroConversionRate()).isNull();
        WireMockManager.stopHnbInvalidStub();
    }

    @Test
    void fetchTodaysEuroConversionRate_serviceIsDown() {
        assertThat(hnbCommunicator.fetchTodaysEuroConversionRate()).isNull();
    }
}
