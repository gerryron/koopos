package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import com.gerryron.koopos.grocerystoreservice.shared.request.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TransactionControllerTest {

    @Autowired
    private ServletWebServerApplicationContext webServerApplicationContext;

    @BeforeEach
    void setUp() {
        port = webServerApplicationContext.getWebServer().getPort();
    }

    @Test
    @Sql("classpath:data/db/product.sql")
    void shouldCreateTransaction_ReturnSuccess() {
        TransactionRequest.ProductPurchased productPurchased = new TransactionRequest.ProductPurchased();
        productPurchased.setProductId(1);
        productPurchased.setAmount(4);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionNumber(UUID.randomUUID().toString());
        transactionRequest.setProductsPurchased(Collections.singletonList(productPurchased));

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(transactionRequest)
                .when()
                .post("/api/transaction")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data", nullValue())
                .body("errorDetails", nullValue());
    }

    @Test
    @Sql("classpath:data/db/product.sql")
    @Sql("classpath:data/db/transaction.sql")
    @Sql("classpath:data/db/transaction_details.sql")
    void shouldGetPaginatedTransaction_ReturnSuccess() {
        given()
                .when()
                .get("/api/transaction")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()));
    }
}