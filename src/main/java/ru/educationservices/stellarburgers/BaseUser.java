package ru.educationservices.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BaseUser {

    private static final String REGISTER = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";
    private static final String DELETE = "/api/auth/user";
    private static final String INFO_USER = "/api/auth/user";

    @Step("Cоздание пользователя")
    public Response createUser(User user) {

        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(REGISTER);
    }

    @Step("Авторизация пользователя")
    public Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(LOGIN);
    }

    @Step("Удаление созданного пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE);
}

    @Step("Получение информации пользователя")
    public Response getUserInfo (String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(INFO_USER);
    }

    @Step("Обновление информации пользователя")
    public Response updateUserInfo (String accessToken, User user) {
        return given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(INFO_USER);
    }

    @Step("Получение информации пользователя - без авторизации")
    public Response getUserInfoNotAuth () {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(INFO_USER);
    }

    @Step("Обновление информации пользователя - без авторизации")
    public Response updateUserInfoNotAuth (User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(INFO_USER);
    }
}