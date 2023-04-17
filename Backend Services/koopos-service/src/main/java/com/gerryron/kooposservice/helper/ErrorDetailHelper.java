package com.gerryron.kooposservice.helper;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.dto.request.ProductRequest;

import java.util.Collections;
import java.util.List;

public class ErrorDetailHelper {

    public static List<ErrorDetail> barcodeAlreadyExists() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(ProductRequest.class.getSimpleName())
                .field("Barcode")
                .message("Product Already Exists")
                .build());
    }

    public static List<ErrorDetail> barcodeNotFound() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(String.class.getSimpleName())
                .field("Barcode")
                .message("Barcode not found")
                .build());
    }
}
