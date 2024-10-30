package io.assesment.currencyexchangeanddiscountcalculation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.assesment.currencyexchangeanddiscountcalculation.exception.BillCalculationException;
import io.assesment.currencyexchangeanddiscountcalculation.exception.InvalidInputException;
import io.assesment.currencyexchangeanddiscountcalculation.util.CurrencyValidator;
import io.assesment.currencyexchangeanddiscountcalculation.util.UserType;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class BillService {

    private final CurrencyService currencyService;
    private final DiscountService discountService;

    @Autowired
    public BillService(CurrencyService currencyService, DiscountService discountService) {
        this.currencyService = currencyService;
        this.discountService = discountService;
    }

    public BigDecimal calculatePayableAmount(BigDecimal amount, String userType, int tenure, 
            String originalCurrency, String targetCurrency, boolean isGrocery) {
        validateInputs(amount, userType, originalCurrency, targetCurrency);
        
        try {
            log.debug("Calculating bill for amount: {}, userType: {}, currency conversion: {} -> {}", 
                    amount, userType, originalCurrency, targetCurrency);

            BigDecimal discount = discountService.calculateDiscount(amount, userType, tenure, isGrocery);
            BigDecimal discountedAmount = amount.subtract(discount);

            BigDecimal exchangeRate = currencyService.getExchangeRate(originalCurrency, targetCurrency);
            BigDecimal finalAmount = discountedAmount.multiply(exchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);

            log.debug("Calculation completed. Final amount: {}", finalAmount);
            return finalAmount;
            
        } catch (Exception e) {
            log.error("Error calculating payable amount", e);
            throw new BillCalculationException("Error calculating payable amount", e);
        }
    }

    private void validateInputs(BigDecimal amount, String userType, 
            String originalCurrency, String targetCurrency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }
        if (!UserType.isValid(userType)) {
            throw new InvalidInputException("Invalid user type: " + userType);
        }
        if (!CurrencyValidator.isValid(originalCurrency) || !CurrencyValidator.isValid(targetCurrency)) {
            throw new InvalidInputException("Invalid currency format");
        }
    }
}
