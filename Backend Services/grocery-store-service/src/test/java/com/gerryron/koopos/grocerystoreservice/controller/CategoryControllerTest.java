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
import static org.hamcrest.Matchers.*;

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
    @SqlGroup({
            @Sql("classpath:data/db/inventory.sql"),
            @Sql("classpath:data/db/category.sql"),
            @Sql("classpath:data/db/inventory_categories.sql")
    })
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
                .body("data[0].categoryName", is("Category A"))
                .body("data[1].categoryName", is("Category B"))
                .body("detailPages.page", is(1))
                .body("detailPages.rowPerPage", is(10))
                .body("detailPages.totalData", is(2));
    }

    @Test
    @Tag("getCategory")
    @SqlGroup({
            @Sql("classpath:data/db/inventory.sql"),
            @Sql("classpath:data/db/category.sql"),
            @Sql("classpath:data/db/inventory_categories.sql")
    })
    void shouldGetCategoryByIdReturnOK() {
        given()
                .queryParam("id", "1")
                .when()
                .get("/api/categories/category")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(SUCCESS.getMessage()))
                .body("data.id", is(1))
                .body("data.categoryName", is("Category A"))
                .body("data.inventories", hasSize(1))
                .body("data.errorDetails", nullValue());
    }

    @Test
    @Tag("getCategory")
    @SqlGroup({
            @Sql("classpath:data/db/inventory.sql"),
            @Sql("classpath:data/db/category.sql"),
            @Sql("classpath:data/db/inventory_categories.sql")
    })
    void shouldGetCategoryByCategoryNameReturnOK() {
        given()
                .queryParam("categoryName", "Category A")
                .when()
                .get("/api/categories/category")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(SUCCESS.getMessage()))
                .body("data.id", is(1))
                .body("data.categoryName", is("Category A"))
                .body("data.inventories", hasSize(1))
                .body("data.errorDetails", nullValue());
    }

    @Test
    @Tag("updateCategory")
    @Sql("classpath:data/db/category.sql")
    void shouldUpdateCategoryReturnOK() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", "1")
                .body(new Category("Category A Updated"))
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

    @Test
    @Tag("deleteCategory")
    @Sql("classpath:data/db/category.sql")
    void shouldDeleteCategoryReturnOK() {
        given()
                .pathParam("id", "1")
                .when()
                .delete("/api/categories/category/{id}")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("responseStatus.responseCode", is(SUCCESS.getCode()))
                .body("responseStatus.responseMessage", is(SUCCESS.getMessage()))
                .body("data", nullValue())
                .body("detailsError", nullValue());
    }

    @AfterEach
    void tearDown() {
        reset();
    }
}
