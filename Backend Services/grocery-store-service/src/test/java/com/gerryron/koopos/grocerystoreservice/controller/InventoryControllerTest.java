package com.gerryron.koopos.grocerystoreservice.controller;


import com.gerryron.koopos.grocerystoreservice.dto.Category;
import com.gerryron.koopos.grocerystoreservice.dto.Item;
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
class InventoryControllerTest {

    @Autowired
    private ServletWebServerApplicationContext webServerApplicationContext;

    @Test
    @Tag("saveItem")
    void shouldSaveItemReturnOK() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new Item("AA21", "Item A", "Item A Description", 20,
                        new BigDecimal(2800), new BigDecimal(3000), null))
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/inventory")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.itemName", is("Item A"))
                .body("data.quantity", is(20))
                .body("detailsError", nullValue());
    }

    @Test
    @Tag("saveItem")
    void shouldSaveItemWithCategoriesReturnOK() {
        Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2800), new BigDecimal(3000), null);
        expectedItem.setCategories(Collections.singleton(new Category("Category A")));

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedItem)
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/inventory")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.itemName", is(expectedItem.getItemName()))
                .body("data.quantity", is(expectedItem.getQuantity()));
    }

    @Test
    @Tag("saveItem")
    @Sql("classpath:data/db/inventory.sql")
    void shouldSaveItemReturnItemAlreadyExists() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new Item("AA21", "Item A", "Item A Description", 20,
                        new BigDecimal(2800), new BigDecimal(3000), null))
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/inventory")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("responseStatus.responseCode", is(ApplicationCode.ITEM_ALREADY_EXISTS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.ITEM_ALREADY_EXISTS.getMessage()))
                .body("data", nullValue())
                .body("detailsError", nullValue());
    }

    @Test
    @Tag("saveItem")
    void shouldSaveItemReturnValidationError() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new Item(null, "", null, -20,
                        new BigDecimal(0), new BigDecimal(0), null))
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/api/inventory")
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
    @Tag("getPaginatedInventories")
    @Sql("classpath:data/db/inventory.sql")
    void shouldGetPaginatedInventoriesReturnOK() {
        given()
                .when()
                .get("/api/inventory")
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
    @Tag("getItem")
    @Sql("classpath:data/db/inventory.sql")
    void shouldGetItemReturnOK_Barcode() {
        given()
                .queryParam("barcode", "AA21")
                .when()
                .get("/api/inventory/item")
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
    @Tag("getItem")
    @Sql("classpath:data/db/inventory.sql")
    void shouldGetItemReturnOK_ItemName() {
        given()
                .queryParam("itemName", "Item A")
                .when()
                .get("/api/inventory/item")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(ApplicationCode.SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.SUCCESS.getMessage()))
                .body("data.itemName", is("Item A"))
                .body("detailsError", nullValue());

    }

    @Test
    @Tag("getItem")
    void shouldGetItemReturnInvalidParameter() {
        given()
                .when()
                .get("/api/inventory/item")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("responseStatus.responseCode", is(ApplicationCode.INVALID_PARAMETER.getCode()))
                .body("responseStatus.responseMessage", is(ApplicationCode.INVALID_PARAMETER.getMessage()))
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