package com.gerryron.kooposservice.helper;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.dto.request.CategoryRequest;
import com.gerryron.kooposservice.dto.request.ProductRequest;
import com.gerryron.kooposservice.dto.request.SignInRequest;
import com.gerryron.kooposservice.dto.request.SignUpRequest;

import java.util.Collections;
import java.util.List;

public class ErrorDetailHelper {

    public static List<ErrorDetail> barcodeAlreadyExists() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(ProductRequest.class.getSimpleName())
                .field("Barcode")
                .message("Product already exists")
                .build());
    }

    public static List<ErrorDetail> barcodeNotFound() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(String.class.getSimpleName())
                .field("Barcode")
                .message("Barcode not found")
                .build());
    }

    public static List<ErrorDetail> categoryNameAlreadyExists() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(CategoryRequest.class.getSimpleName())
                .field("categoryName")
                .message("Category already exists")
                .build());
    }

    public static List<ErrorDetail> categoryNameNotFound() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(CategoryRequest.class.getSimpleName())
                .field("categoryName")
                .message("Category name not found")
                .build());
    }

    public static List<ErrorDetail> userAlreadyExists() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(SignUpRequest.class.getSimpleName())
                .field("username or email")
                .message("Username or email already exists")
                .build());
    }

    public static List<ErrorDetail> userNotFound() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(SignInRequest.class.getSimpleName())
                .field("username or email")
                .message("Username or email not found")
                .build());
    }

    public static List<ErrorDetail> userInvalidRole() {
        return Collections.singletonList(ErrorDetail.builder()
                .object(SignUpRequest.class.getSimpleName())
                .field("role")
                .message("User has an invalid role")
                .build());
    }
}
