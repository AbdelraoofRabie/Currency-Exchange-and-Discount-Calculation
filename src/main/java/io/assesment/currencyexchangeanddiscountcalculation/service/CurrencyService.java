package io.assesment.currencyexchangeanddiscountcalculation.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "https://open.er-api.com/v6/latest/";

    @Cacheable("exchangeRates")
    public BigDecimal getExchangeRate(String baseCurrency, String targetCurrency, String apiKey) {
        String url = API_URL + baseCurrency + "?apikey=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);
        Map<String, Object> rates = json.getJSONObject("rates").toMap();
        return new BigDecimal(rates.get(targetCurrency).toString());
    }
}
