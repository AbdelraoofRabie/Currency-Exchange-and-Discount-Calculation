package io.assesment.currencyexchangeanddiscountcalculation.service;

import io.assesment.currencyexchangeanddiscountcalculation.exception.BillCalculationException;
import io.assesment.currencyexchangeanddiscountcalculation.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private BillService billService;

    @Test
    void calculatePayableAmount_ValidInputs_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(discountService.calculateDiscount(eq(amount), eq("employee"), eq(2), eq(false)))
            .thenReturn(new BigDecimal("10.00"));
        when(currencyService.getExchangeRate("USD", "EUR"))
            .thenReturn(new BigDecimal("0.85"));

        // Act
        BigDecimal result = billService.calculatePayableAmount(
            amount, "employee", 2, "USD", "EUR", false);

        // Assert
        assertEquals(new BigDecimal("76.50"), result);
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void calculatePayableAmount_InvalidInputs_ThrowsException(
            BigDecimal amount, String userType, String origCurrency, String targetCurrency) {
        
        assertThrows(InvalidInputException.class, () ->
            billService.calculatePayableAmount(
                amount, userType, 1, origCurrency, targetCurrency, false));
    }

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.of(
            Arguments.of(null, "employee", "USD", "EUR"),
            Arguments.of(new BigDecimal("-100"), "employee", "USD", "EUR"),
            Arguments.of(new BigDecimal("100"), null, "USD", "EUR"),
            Arguments.of(new BigDecimal("100"), "invalid", "USD", "EUR"),
            Arguments.of(new BigDecimal("100"), "employee", null, "EUR"),
            Arguments.of(new BigDecimal("100"), "employee", "INVALID", "EUR"),
            Arguments.of(new BigDecimal("100"), "employee", "USD", null),
            Arguments.of(new BigDecimal("100"), "employee", "USD", "INVALID")
        );
    }

    @Test
    void calculatePayableAmount_CurrencyServiceError_ThrowsException() {
        // Arrange
        when(discountService.calculateDiscount(any(), any(), anyInt(), anyBoolean()))
            .thenReturn(BigDecimal.ZERO);
        when(currencyService.getExchangeRate(anyString(), anyString()))
            .thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        assertThrows(BillCalculationException.class, () ->
            billService.calculatePayableAmount(
                new BigDecimal("100"), "employee", 1, "USD", "EUR", false));
    }

    @Test
    void calculatePayableAmount_DiscountServiceError_ThrowsException() {
        // Arrange
        when(discountService.calculateDiscount(any(), any(), anyInt(), anyBoolean()))
            .thenThrow(new RuntimeException("Discount calculation error"));

        // Act & Assert
        assertThrows(BillCalculationException.class, () ->
            billService.calculatePayableAmount(
                new BigDecimal("100"), "employee", 1, "USD", "EUR", false));
    }
} 