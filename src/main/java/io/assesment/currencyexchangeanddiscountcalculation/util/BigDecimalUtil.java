package io.assesment.currencyexchangeanddiscountcalculation.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b).setScale(2, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b).setScale(2, RoundingMode.HALF_UP);
    }
} 