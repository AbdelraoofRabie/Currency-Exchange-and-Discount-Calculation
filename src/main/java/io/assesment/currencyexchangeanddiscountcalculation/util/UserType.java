package io.assesment.currencyexchangeanddiscountcalculation.util;

public class UserType {
    public static final String EMPLOYEE = "employee";
    public static final String AFFILIATE = "affiliate";
    public static final String CUSTOMER = "customer";

    public static boolean isValid(String userType) {
        return (
                EMPLOYEE.equals(userType) ||
                        AFFILIATE.equals(userType) ||
                        CUSTOMER.equals(userType)
        );
    }
} 