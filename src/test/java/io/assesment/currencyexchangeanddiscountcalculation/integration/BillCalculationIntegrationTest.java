package io.assesment.currencyexchangeanddiscountcalculation.integration;

import io.assesment.currencyexchangeanddiscountcalculation.dto.BillCalculationRequest;
import io.assesment.currencyexchangeanddiscountcalculation.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BillCalculationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/calculate";
    }

    @Test
    void calculateBill_ValidRequest_ReturnsSuccess() {
        // Arrange
        BillCalculationRequest request = new BillCalculationRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setUserType("employee");
        request.setTenure(2);
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setGrocery(false);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate
            .withBasicAuth("user", "password")
            .postForEntity(getBaseUrl(), request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void calculateBill_InvalidAuth_Returns401() {
        // Arrange
        BillCalculationRequest request = new BillCalculationRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setUserType("employee");
        request.setTenure(2);
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setGrocery(false);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate
            .withBasicAuth("wrong", "credentials")
            .postForEntity(getBaseUrl(), request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void calculateBill_InvalidRequest_ReturnsBadRequest() {
        // Arrange
        BillCalculationRequest request = new BillCalculationRequest();
        request.setAmount(new BigDecimal("-100.00")); // Invalid amount
        request.setUserType("invalid"); // Invalid user type
        request.setTenure(2);
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setGrocery(false);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate
            .withBasicAuth("user", "password")
            .postForEntity(getBaseUrl(), request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void calculateBill_InvalidCurrency_ReturnsBadRequest() {
        // Arrange
        BillCalculationRequest request = new BillCalculationRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setUserType("employee");
        request.setTenure(2);
        request.setOriginalCurrency("INVALID");
        request.setTargetCurrency("EUR");
        request.setGrocery(false);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate
            .withBasicAuth("user", "password")
            .postForEntity(getBaseUrl(), request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        headers.add("Authorization", "Basic " +
                Base64.getEncoder().encodeToString(auth.getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    @Test
    void calculateBill_GroceryPurchase_ReturnsCorrectDiscount() {
        // Arrange
        BillCalculationRequest request = new BillCalculationRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setUserType("employee");
        request.setTenure(2);
        request.setOriginalCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setGrocery(true);

        HttpEntity<BillCalculationRequest> entity = new HttpEntity<>(request,
                createHeaders("user", "password"));

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"success\":true"));
    }
} 