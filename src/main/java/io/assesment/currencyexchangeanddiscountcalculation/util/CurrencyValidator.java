package io.assesment.currencyexchangeanddiscountcalculation.util;

import lombok.extern.slf4j.Slf4j;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class CurrencyValidator {
    private static final Set<String> VALID_CURRENCIES = initializeCurrencies();
    
    private CurrencyValidator() {} // Prevent instantiation

    private static Set<String> initializeCurrencies() {
        return Currency.getAvailableCurrencies()
                .stream()
                .map(Currency::getCurrencyCode)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static boolean isValid(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            log.debug("Invalid currency code: null or empty");
            return false;
        }
        boolean isValid = VALID_CURRENCIES.contains(currencyCode.toUpperCase());
        if (!isValid) {
            log.debug("Invalid currency code: {}", currencyCode);
        }
        return isValid;
    }
} 