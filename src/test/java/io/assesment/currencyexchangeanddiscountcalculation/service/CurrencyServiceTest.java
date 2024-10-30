package io.assesment.currencyexchangeanddiscountcalculation.service;

import io.assesment.currencyexchangeanddiscountcalculation.exception.CurrencyExchangeException;
import io.github.resilience4j.ratelimiter.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock(lenient = true)
    private RestTemplate restTemplate;
    
    @Mock(lenient = true)
    private RateLimiter rateLimiter;

    private CurrencyService currencyService;
    private static final String API_URL = "https://api.example.com/";
    private static final String API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        currencyService = new CurrencyService(restTemplate, API_URL, API_KEY, rateLimiter);
        when(rateLimiter.acquirePermission()).thenReturn(true);
    }

    @Test
    void getExchangeRate_SuccessfulResponse_ReturnsCorrectRate() {
        // Arrange
        String responseBody = """
            {
                "rates": {
                    "EUR": 0.85
                }
            }
            """;
        when(rateLimiter.acquirePermission()).thenReturn(true);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // Act
        BigDecimal rate = currencyService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(new BigDecimal("0.85"), rate);
    }

    @Test
    void getExchangeRate_RateLimitExceeded_ThrowsException() {
        // Arrange
        when(rateLimiter.acquirePermission()).thenReturn(false);

        // Act & Assert
        assertThrows(CurrencyExchangeException.class, () ->
            currencyService.getExchangeRate("USD", "EUR"));
    }

    @Test
    void getExchangeRate_ApiError_ThrowsException() {
        // Arrange
        when(rateLimiter.acquirePermission()).thenReturn(true);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("API Error"));

        // Act & Assert
        assertThrows(CurrencyExchangeException.class, () ->
            currencyService.getExchangeRate("USD", "EUR"));
    }

    @Test
    void getExchangeRate_InvalidResponse_ThrowsException() {
        // Arrange
        String invalidResponse = "{ \"rates\": {} }";
        when(rateLimiter.acquirePermission()).thenReturn(true);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(invalidResponse, HttpStatus.OK));

        // Act & Assert
        assertThrows(CurrencyExchangeException.class, () ->
            currencyService.getExchangeRate("USD", "EUR"));
    }

    @Test
    void getExchangeRate_NonSuccessStatusCode_ThrowsException() {
        // Arrange
        when(rateLimiter.acquirePermission()).thenReturn(true);
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // Act & Assert
        assertThrows(CurrencyExchangeException.class, () ->
            currencyService.getExchangeRate("USD", "EUR"));
    }
} 