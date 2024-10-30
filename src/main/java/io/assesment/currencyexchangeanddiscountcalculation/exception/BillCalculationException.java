package io.assesment.currencyexchangeanddiscountcalculation.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BillCalculationException extends RuntimeException {
    public BillCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}