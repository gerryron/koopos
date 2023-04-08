package com.gerryron.koopos.grocerystoreservice.controller;


import com.gerryron.koopos.grocerystoreservice.shared.dto.Product;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Collections;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    private ServletWebServerApplicationContext webServerApplicationContext;

    @Test
    @Tag("saveProduct")
    void shouldSaveProductReturnOK() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new Product("AA21", "Product A", "Product A Description", 20,
                        new BigDecimal(2800), new BigDecimal(3000), null))
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/products")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.productName", is("Product A"))
                .body("data.quantity", is(20))
                .body("detailsError", nullValue());
    }

    @Test
    @Tag("saveProduct")
    void shouldSaveProductWithCategoriesReturnOK() {
        Product expectedProduct = new Product("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2800), new BigDecimal(3000), null);
        expectedProduct.setCategories(Collections.singleton("Category A"));

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedProduct)
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/products")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.productName", is(expectedProduct.getProductName()))
                .body("data.quantity", is(expectedProduct.getQuantity()));
    }

    @Test
    @Tag("saveProduct")
    @Sql("classpath:data/db/product.sql")
    void shouldSaveProductReturnProductAlreadyExists() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new Product("AA21", "Product A", "Product A Description", 20,
                        new BigDecimal(2800), new BigDecimal(3000), null))
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/products")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("responseStatus.responseCode", is(ApplicationCode.PRODUCT_ALREADY_EXISTS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.PRODUCT_ALREADY_EXISTS.getMessage()))
                .body("data", nullValue())
                .body("detailsError", nullValue());
    }

    @Test
    @Tag("saveProduct")
    void shouldSaveProductReturnValidationError() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new Product(null, "", null, -20,
                        new BigDecimal(0), new BigDecimal(0), null))
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/products")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("responseStatus.responseCode", is(ApplicationCode.VALIDATION_ERROR.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.VALIDATION_ERROR.getMessage()))
                .body("data", nullValue())
                .body("errorDetails", hasSize(6));
    }

    @Test
    @Tag("getPaginatedProducts")
    @Sql("classpath:data/db/product.sql")
    void shouldGetPaginatedProductsReturnOK() {
        given()
                .when()
                .get("/api/products")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data", hasSize(2))
                .body("detailPages.page", is(1))
                .body("detailPages.rowPerPage", is(10))
                .body("detailPages.totalData", is(2));
    }

    @Test
    @Tag("getProduct")
    @Sql("classpath:data/db/product.sql")
    void shouldGetProductReturnOK_Barcode() {
        given()
                .queryParam("barcode", "AA21")
                .when()
                .get("/api/products/product")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.barcode", is("AA21"))
                .body("detailsError", nullValue());

    }

    @Test
    @Tag("getProduct")
    @Sql("classpath:data/db/product.sql")
    void shouldGetProductReturnOK_ProductName() {
        given()
                .queryParam("productName", "Product A")
                .when()
                .get("/api/products/product")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.productName", is("Product A"))
                .body("detailsError", nullValue());

    }

    @Test
    @Tag("getProduct")
    void shouldGetProductReturnInvalidParameter() {
        given()
                .when()
                .get("/api/products/product")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("responseStatus.responseCode", is(ApplicationCode.INVALID_PARAMETER.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.INVALID_PARAMETER.getMessage()))
                .body("data", nullValue())
                .body("detailsError", nullValue());

    }

    @Test
    @Tag("putProduct")
    @Sql("classpath:data/db/product.sql")
    void shouldPutProductReturnSuccess() {
        given()
                .pathParam("barcode", "AA21")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new Product("AA21", "Product A Update", "Product A Description Updated", 6,
                        new BigDecimal(12800), new BigDecimal(13000), Collections.singleton("Category A")))
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/api/products/product/{barcode}")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.barcode", is("AA21"))
                .body("data.productName", is("Product A Update"))
                .body("data.description", is("Product A Description Updated"))
                .body("data.quantity", is(6))
                .body("data.buyingPrice", is(12800))
                .body("data.sellingPrice", is(13000))
                .body("data.categories.size()", is(1))
                .body("detailsError", nullValue());
    }

    @Test
    @Tag("deleteProduct")
    @Sql("classpath:data/db/product.sql")
    void shouldDeleteProductReturnSuccess() {
        given()
                .pathParam("barcode", "AA21")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/api/products/product/{barcode}")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data", nullValue())
                .body("detailsError", nullValue());
    }

    @BeforeEach
    void setUp() {
        port = webServerApplicationContext.getWebServer().getPort();
    }

    @AfterEach
    void tearDown() {
        reset();
    }
}