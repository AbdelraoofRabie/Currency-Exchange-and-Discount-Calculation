package io.assesment.currencyexchangeanddiscountcalculation.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import io.assesment.currencyexchangeanddiscountcalculation.exception.CurrencyExchangeException;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;
import io.github.resilience4j.ratelimiter.RateLimiter;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import java.math.BigDecimal;
import java.util.Map;
import java.time.Duration;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CurrencyService {

    private static final Set<String> VALID_CURRENCIES = Currency.getAvailableCurrencies()
            .stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final RateLimiter rateLimiter;

    public CurrencyService(
            RestTemplate restTemplate,
            @Value("${currency.api.url:https://open.er-api.com/v6/latest/}") String apiUrl,
            @Value("${currency.api.key:https://open.er-api.com/v6/latest/}") String apiKey,
            RateLimiter rateLimiter
            ) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.rateLimiter = rateLimiter;
        // this.rateLimiter = RateLimiter.of("currency-service",
        //     RateLimiterConfig.custom()
        //         .limitForPeriod(10)
        //         .limitRefreshPeriod(Duration.ofSeconds(1))
        //         .build());
    }

    @Cacheable(value = "exchangeRates", key = "#baseCurrency + '_' + #targetCurrency")
    @Retryable(
        value = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public BigDecimal getExchangeRate(String baseCurrency, String targetCurrency) {
        validateCurrencies(baseCurrency, targetCurrency, apiKey);
        
        if (!rateLimiter.acquirePermission()) {
            throw new CurrencyExchangeException("Rate limit exceeded");
        }

        try {
            String url = buildUrl(baseCurrency, apiKey);
            log.debug("Fetching exchange rate from: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new CurrencyExchangeException("Failed to fetch exchange rate: " + response.getStatusCode());
            }

            JSONObject json = new JSONObject(response.getBody());
            Map<String, Object> rates = json.getJSONObject("rates").toMap();
            
            Object rate = rates.get(targetCurrency);
            if (rate == null) {
                throw new CurrencyExchangeException("Exchange rate not found for currency: " + targetCurrency);
            }

            return new BigDecimal(rate.toString());
        } catch (RestClientException e) {
            log.error("Error fetching exchange rate for {} to {}", baseCurrency, targetCurrency, e);
            throw new CurrencyExchangeException("Failed to fetch exchange rate", e);
        } catch (NumberFormatException e) {
            log.error("Error parsing exchange rate", e);
            throw new CurrencyExchangeException("Invalid exchange rate format", e);
        }
    }

    private void validateCurrencies(String baseCurrency, String targetCurrency, String apiKey) {
        if (baseCurrency == null || targetCurrency == null || apiKey == null) {
            throw new IllegalArgumentException("Currency parameters and API key cannot be null");
        }
        
        if (!VALID_CURRENCIES.contains(baseCurrency)) {
            throw new IllegalArgumentException("Invalid base currency: " + baseCurrency);
        }
        
        if (!VALID_CURRENCIES.contains(targetCurrency)) {
            throw new IllegalArgumentException("Invalid target currency: " + targetCurrency);
        }
    }

    private String buildUrl(String baseCurrency, String apiKey) {
        return apiUrl + baseCurrency + "?apikey=" + apiKey;
    }
}
