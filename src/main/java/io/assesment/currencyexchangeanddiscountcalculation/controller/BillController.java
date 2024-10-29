package io.assesment.currencyexchangeanddiscountcalculation.controller;

import io.assesment.currencyexchangeanddiscountcalculation.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping("/calculate")
    public BigDecimal calculateBill(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String userType = request.get("userType").toString();
        int tenure = Integer.parseInt(request.get("tenure").toString());
        String originalCurrency = request.get("originalCurrency").toString();
        String targetCurrency = request.get("targetCurrency").toString();
        boolean isGrocery = Boolean.parseBoolean(request.get("isGrocery").toString());
        String apiKey = "your-api-key"; // Replace with actual API key

        return billService.calculatePayableAmount(amount, userType, tenure, originalCurrency, targetCurrency, isGrocery, apiKey);
    }
}
