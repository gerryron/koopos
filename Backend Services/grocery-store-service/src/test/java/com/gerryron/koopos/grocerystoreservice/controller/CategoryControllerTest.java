package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.dto.Category;
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
import org.springframework.test.context.jdbc.SqlGroup;

import static com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode.SUCCESS;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CategoryControllerTest {

    @Autowired
    private ServletWebServerApplicationContext webServerApplicationContext;

    @BeforeEach
    void setUp() {
        port = webServerApplicationContext.getWebServer().getPort();
    }

    @Test
    @Tag("getPaginatedCategories")
    @Sql("classpath:data/db/category.sql")
    void shouldGetPaginatedCategoriesReturnOK() {
        given()
                .when()
                .get("/api/categories")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(SUCCESS.getMessage()))
                .body("data", hasSize(2))
                .body("detailPages.page", is(1))
                .body("detailPages.rowPerPage", is(10))
                .body("detailPages.totalData", is(2));
    }

    @Test
    @Tag("getItemByCategoryName")
    @SqlGroup({
            @Sql("classpath:data/db/inventory.sql"),
            @Sql("classpath:data/db/category.sql"),
            @Sql("classpath:data/db/inventory_categories.sql")
    })
    void shouldGetItemByCategoryNameReturnOK() {
        given()
                .pathParam("categoryName", "Category A")
                .when()
                .get("/api/categories/category/{categoryName}")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(SUCCESS.getMessage()))
                .body("data", hasSize(1))
                .body("data[0].barcode", is("AA21"))
                .body("data[0].itemName", is("Item A"));
    }

    @Test
    @Tag("updateCategory")
    @Sql("classpath:data/db/category.sql")
    void shouldUpdateCategoryReturnOK() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", "1")
                .body(new Category(1, "Category A Updated"))
                .when()
                .put("/api/categories/category/{id}")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(SUCCESS.getMessage()))
                .body("data.id", is(1))
                .body("data.categoryName", is("Category A Updated"));
    }

    @AfterEach
    void tearDown() {
        reset();
    }
}
