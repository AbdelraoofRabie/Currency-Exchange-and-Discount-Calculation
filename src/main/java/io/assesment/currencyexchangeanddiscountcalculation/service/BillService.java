package io.assesment.currencyexchangeanddiscountcalculation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BillService {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private DiscountService discountService;

    public BigDecimal calculatePayableAmount(BigDecimal amount, String userType, int tenure, String originalCurrency, String targetCurrency, boolean isGrocery, String apiKey) {
        BigDecimal discount = discountService.calculateDiscount(amount, userType, tenure, isGrocery);
        BigDecimal discountedAmount = amount.subtract(discount);

        BigDecimal exchangeRate = currencyService.getExchangeRate(originalCurrency, targetCurrency, apiKey);
        return discountedAmount.multiply(exchangeRate);
    }
}
