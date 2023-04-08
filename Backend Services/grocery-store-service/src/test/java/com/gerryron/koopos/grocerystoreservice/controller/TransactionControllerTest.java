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
import static org.hamcrest.Matchers.*;

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
    void shouldGetPaginatedTransactions_ReturnSuccess() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/transaction")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data", hasSize(1))
                .body("data[0].transactionNumber", is("transactionNumber01"))
                .body("data[0].amount", is(2))
                .body("data[0].totalPrice", is(15500.0F))
                .body("data[0].profit", is(1000.00F))
                .body("data[0].transactionDetails", hasSize(2))
                .body("data[0].transactionDetails[0].id", is(1))
                .body("data[0].transactionDetails[0].productName", is("Product A"))
                .body("data[0].transactionDetails[0].amount", is(2))
                .body("data[0].transactionDetails[0].price", is(3000.00F))
                .body("data[0].transactionDetails[0].createdDate", notNullValue())
                .body("data[0].transactionDetails[1].id", is(2))
                .body("data[0].transactionDetails[1].productName", is("Product B"))
                .body("data[0].transactionDetails[1].amount", is(1))
                .body("data[0].transactionDetails[1].price", is(9500.00F))
                .body("data[0].transactionDetails[1].createdDate", notNullValue())
                .body("detailPages.page", is(1))
                .body("detailPages.rowPerPage", is(10))
                .body("detailPages.totalData", is(1));
    }

    @Test
    @Sql("classpath:data/db/product.sql")
    @Sql("classpath:data/db/transaction.sql")
    @Sql("classpath:data/db/transaction_details.sql")
    void shouldGetTransaction_ReturnSuccess() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("transactionNumber", "transactionNumber01")
                .log().all()
                .when()
                .get("/api/transaction/{transactionNumber}")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.transactionNumber", is("transactionNumber01"))
                .body("data.amount", is(2))
                .body("data.totalPrice", is(15500.0F))
                .body("data.profit", is(1000.00F))
                .body("data.transactionDetails", hasSize(2))
                .body("data.transactionDetails[0].id", is(1))
                .body("data.transactionDetails[0].productName", is("Product A"))
                .body("data.transactionDetails[0].amount", is(2))
                .body("data.transactionDetails[0].price", is(3000.00F))
                .body("data.transactionDetails[0].createdDate", notNullValue())
                .body("data.transactionDetails[1].id", is(2))
                .body("data.transactionDetails[1].productName", is("Product B"))
                .body("data.transactionDetails[1].amount", is(1))
                .body("data.transactionDetails[1].price", is(9500.00F))
                .body("data.transactionDetails[1].createdDate", notNullValue())
                .body("data.createdDate", notNullValue())
                .body("errorDetails", nullValue());
    }
}