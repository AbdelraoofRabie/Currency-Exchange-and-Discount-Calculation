package io.assesment.currencyexchangeanddiscountcalculation.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DiscountService {
    private static final BigDecimal EMPLOYEE_DISCOUNT = new BigDecimal("0.30");
    private static final BigDecimal AFFILIATE_DISCOUNT = new BigDecimal("0.10");
    private static final BigDecimal LOYALTY_DISCOUNT = new BigDecimal("0.05");
    private static final BigDecimal BILL_DISCOUNT_RATE = new BigDecimal("5.00");
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    
    /**
     * Calculates the total discount for a purchase.
     *
     * @param amount The purchase amount
     * @param userType The type of user (employee, affiliate, or regular)
     * @param customerTenure Years as a customer
     * @param isGrocery Whether the purchase is for grocery items
     * @return The calculated discount amount
     * @throws IllegalArgumentException if amount is null or negative
     */
    public BigDecimal calculateDiscount(BigDecimal amount, String userType, int customerTenure, boolean isGrocery) {
        validateInput(amount, userType);
        
        BigDecimal userDiscount = calculateUserDiscount(amount, userType, customerTenure, isGrocery);
        BigDecimal billDiscount = calculateBillDiscount(amount);
        
        return userDiscount.add(billDiscount).setScale(2, RoundingMode.HALF_UP);
    }
    
    private void validateInput(BigDecimal amount, String userType) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-null and non-negative");
        }
        if (userType == null || userType.trim().isEmpty()) {
            throw new IllegalArgumentException("User type must not be null or empty");
        }
    }
    
    private BigDecimal calculateUserDiscount(BigDecimal amount, String userType, int customerTenure, boolean isGrocery) {
        if (isGrocery) {
            return BigDecimal.ZERO;
        }
        
        return switch (userType.toLowerCase()) {
            case "employee" -> amount.multiply(EMPLOYEE_DISCOUNT);
            case "affiliate" -> amount.multiply(AFFILIATE_DISCOUNT);
            default -> customerTenure > 2 ? amount.multiply(LOYALTY_DISCOUNT) : BigDecimal.ZERO;
        };
    }
    
    private BigDecimal calculateBillDiscount(BigDecimal amount) {
        return amount.divide(HUNDRED).multiply(BILL_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
