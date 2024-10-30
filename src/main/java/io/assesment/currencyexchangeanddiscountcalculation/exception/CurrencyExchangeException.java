package io.assesment.currencyexchangeanddiscountcalculation.exception;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CurrencyExchangeException extends RuntimeException {
    public CurrencyExchangeException(String message) {
        super(message);
    }

    public CurrencyExchangeException(String message, Throwable cause) {
        super(message, cause);
    }
} 