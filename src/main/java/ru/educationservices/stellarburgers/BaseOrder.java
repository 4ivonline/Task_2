package ru.educationservices.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BaseOrder {

    private static final String CREATE_ORDER = "/api/orders";

    @Step("Создание заказа")
    public Response createOrderInfo (Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(CREATE_ORDER);
    }

    @Step("Получение заказов пользователя")
    public Response getOrdersUser (String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get(CREATE_ORDER);
    }

    @Step("Создание заказа - без авторизации")
    public Response createOrderInfoNoAuth (Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(CREATE_ORDER);
    }

    @Step("Получение заказов пользователя - без авторизации")
    public Response getOrdersUserNoAuth () {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(CREATE_ORDER);
    }
}