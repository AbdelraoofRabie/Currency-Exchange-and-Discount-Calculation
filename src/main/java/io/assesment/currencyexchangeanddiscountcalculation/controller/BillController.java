package io.assesment.currencyexchangeanddiscountcalculation.controller;

import io.assesment.currencyexchangeanddiscountcalculation.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.assesment.currencyexchangeanddiscountcalculation.dto.ApiResponse;
import io.assesment.currencyexchangeanddiscountcalculation.dto.BillCalculationRequest;
import io.assesment.currencyexchangeanddiscountcalculation.exception.InvalidInputException;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BillController {

    private final BillService billService;
    
    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse> calculateBill(@Valid @RequestBody BillCalculationRequest request) {
        try {
            BigDecimal result = billService.calculatePayableAmount(
                request.getAmount(),
                request.getUserType(),
                request.getTenure(),
                request.getOriginalCurrency(),
                request.getTargetCurrency(),
                request.isGrocery()
            );
            
            return ResponseEntity.ok(new ApiResponse(true, "Calculation successful", result));
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "An error occurred during calculation", null));
        }
    }
}
