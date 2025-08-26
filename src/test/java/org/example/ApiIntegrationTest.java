package org.example;

import org.example.config.TestBase;
import org.example.services.ApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiIntegrationTest extends TestBase {

    private ApiService apiService;

    @BeforeEach
    void setup()
    {
        apiService = new ApiService(getApiBaseUrl(), dataBaseService);

    }

    @Test
    void testApiInsertsIntoDatabase()
    {
        //API call
        wireMockServer.stubFor(get(urlEqualTo("/api/data/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"name\":\"John Doe\"}")));

        apiService.fetchAndInsert(1);
        Assertions.assertTrue(dataBaseService.recordExists(1, "John Doe"));
    }

    @Test
    void testApiFetchesUserName()
    {
        wireMockServer.stubFor(get(urlEqualTo("/api/getName/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":2,\"name\":\"Jane Doe\"}")));

        apiService.fetchAndInsert(2);
        Assertions.assertEquals("Jane Doe", apiService.getUserName(2));
    }
}
