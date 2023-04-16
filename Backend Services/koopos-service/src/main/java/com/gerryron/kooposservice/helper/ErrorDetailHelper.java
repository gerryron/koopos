package com.gerryron.kooposservice.helper;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.dto.request.ProductRequest;

public class ErrorDetailHelper {

    public static ErrorDetail barcodeAlreadyExists() {
        return ErrorDetail.builder()
                .object(ProductRequest.class.getSimpleName())
                .field("Barcode")
                .message("Product Already Exists")
                .build();
    }

    public static ErrorDetail barcodeNotFound() {
        return ErrorDetail.builder()
                .object(String.class.getSimpleName())
                .field("Barcode")
                .message("Barcode not found")
                .build();
    }
}
