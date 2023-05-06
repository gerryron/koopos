package com.gerryron.kooposservice.helper;

import com.gerryron.kooposservice.dto.ErrorDetail;

import java.util.Collections;
import java.util.List;

public class ErrorDetailHelper {

    public static ErrorDetail notFound(String property) {
        return ErrorDetail.builder()
                .property(property)
                .message(property + " not found")
                .build();
    }

    public static List<ErrorDetail> notFoundSingletonList(String property) {
        return Collections.singletonList(notFound(property));
    }

    public static ErrorDetail alreadyExists(String property) {
        return ErrorDetail.builder()
                .property(property)
                .message(property + " already exists")
                .build();
    }

    public static List<ErrorDetail> alreadyExistsSingletonList(String property) {
        return Collections.singletonList(alreadyExists(property));
    }

    public static ErrorDetail invalidInput(String property) {
        return ErrorDetail.builder()
                .property(property)
                .message(property + " invalid input")
                .build();
    }

    public static List<ErrorDetail> invalidInputSingletonList(String property) {
        return Collections.singletonList(invalidInput(property));
    }

    public static ErrorDetail productOutOfStock(String productName) {
        return ErrorDetail.builder()
                .property("productsPurchased[].quantity")
                .message("the quantity of " + productName + " products requested in out of stock")
                .build();
    }
}
