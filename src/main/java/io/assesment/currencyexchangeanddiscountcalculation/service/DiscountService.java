package io.assesment.currencyexchangeanddiscountcalculation.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DiscountService {

    public BigDecimal calculateDiscount(BigDecimal amount, String userType, int customerTenure, boolean isGrocery) {
        BigDecimal discount = BigDecimal.ZERO;

        if (!isGrocery) {
            if ("employee".equals(userType)) {
                discount = amount.multiply(BigDecimal.valueOf(0.3));
            } else if ("affiliate".equals(userType)) {
                discount = amount.multiply(BigDecimal.valueOf(0.1));
            } else if (customerTenure > 2) {
                discount = amount.multiply(BigDecimal.valueOf(0.05));
            }
        }

        BigDecimal additionalDiscount = amount.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(5));
        return discount.add(additionalDiscount);
    }
}
