package io.assesment.currencyexchangeanddiscountcalculation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DiscountServiceTest {

    private DiscountService discountService;

    @BeforeEach
    void setUp() {
        discountService = new DiscountService();
    }

    @Test
    void calculateDiscount_EmployeeNonGrocery_Returns30PercentPlusBillDiscount() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal result = discountService.calculateDiscount(amount, "employee", 1, false);
        assertEquals(new BigDecimal("35.00").setScale(2), result.setScale(2));
    }

    @Test
    void calculateDiscount_AffiliateNonGrocery_Returns10PercentPlusBillDiscount() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal result = discountService.calculateDiscount(amount, "affiliate", 1, false);
        assertEquals(new BigDecimal("15.00").setScale(2), result.setScale(2));
    }

    @Test
    void calculateDiscount_LoyalCustomerNonGrocery_Returns5PercentPlusBillDiscount() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal result = discountService.calculateDiscount(amount, "customer", 3, false);
        assertEquals(new BigDecimal("10.00"), result); // 5% loyalty + 5% bill discount
    }

    @Test
    void calculateDiscount_NonLoyalCustomerNonGrocery_ReturnsOnlyBillDiscount() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal result = discountService.calculateDiscount(amount, "customer", 1, false);
        assertEquals(new BigDecimal("5.00"), result); // Only 5% bill discount
    }

    @ParameterizedTest
    @CsvSource({
        "employee, true, 5.00",   // Only bill discount for grocery
        "affiliate, true, 5.00",  // Only bill discount for grocery
        "customer, true, 5.00"    // Only bill discount for grocery
    })
    void calculateDiscount_GroceryItems_ReturnsOnlyBillDiscount(
            String userType, boolean isGrocery, String expectedDiscount) {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal result = discountService.calculateDiscount(amount, userType, 3, isGrocery);
        assertEquals(new BigDecimal(expectedDiscount).setScale(2), result.setScale(2));
    }

    @Test
    void calculateDiscount_NullAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            discountService.calculateDiscount(null, "employee", 1, false));
    }

    @Test
    void calculateDiscount_NegativeAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            discountService.calculateDiscount(new BigDecimal("-100"), "employee", 1, false));
    }

    @Test
    void calculateDiscount_NullUserType_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            discountService.calculateDiscount(new BigDecimal("100"), null, 1, false));
    }

    @Test
    void calculateDiscount_EmptyUserType_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
            discountService.calculateDiscount(new BigDecimal("100"), "", 1, false));
    }
} 