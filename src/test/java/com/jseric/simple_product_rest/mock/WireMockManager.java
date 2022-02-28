package com.jseric.simple_product_rest.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.removeStub;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.http.MediaType;

public class WireMockManager {
    private static StubMapping hnbEurStub;
    private static StubMapping hnbInvalidStub;

    public static void startHnbEurStub() {
        hnbEurStub = stubFor(get("/tecajn/v1?valuta=EUR").willReturn(aResponse()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("[{\"Broj tečajnice\":\"37\",\"Datum primjene\":\"01.01.2022\",\"Država\":\"EMU\",\"Šifra " +
                                  "valute\":\"978\",\"Valuta\":\"EUR\",\"Jedinica\":1,\"Kupovni za devize\":\"7,500000\"," +
                                  "\"Srednji za devize\":\"7,500000\",\"7,500000\":\"7,500000\"}]")));
    }

    public static void stopHnbEurStub() {
        removeStub(hnbEurStub);
    }

    public static void startHnbInvalidStub() {
        hnbInvalidStub = stubFor(get("/tecajn/v1?valuta=EUR").willReturn(aResponse()
                .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("[]")));
    }

    public static void stopHnbInvalidStub() {
        removeStub(hnbInvalidStub);
    }

}
