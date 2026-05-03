package ru.educationservices.stellarburgers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class EditUserTest extends BaseTest {

    private User user;
    private BaseUser baseUser = new BaseUser();
    private String accessToken;

    @BeforeEach
    public void setUpUser() {

        user = new User(
                "usertest001@test.test",
                "1234",
                "Ахилес");

        //Создание пользователя
        accessToken = baseUser.createUser(user)
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    //Удаление пользователя
    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            baseUser.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение данных пользователя - успешно")
    public void getInfoUserSuccess() {

        baseUser.getUserInfo(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение email пользователя - успешно")
    public void updateInfoEmailSuccess() {

        User updateUser = new User(
                "testyou@test.test",
                user.getPassword(),
                user.getName());

        baseUser.updateUserInfo(accessToken, updateUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo("testyou@test.test"))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение name пользователя - успешно")
    public void updateInfoNameSuccess() {

        User updateUser = new User(
                user.getEmail(),
                user.getPassword(),
                "Гектор");

        baseUser.updateUserInfo(accessToken, updateUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo("Гектор"));
    }

    @Test
    @DisplayName("Изменение на email уже существующего пользователя - ошибка 403")
    public void updateExistingEmailFailed() {

        User secondUser = new User(
                "usertest002@test.test",
                "1234",
                "Перс"
        );

        String secondToken = baseUser.createUser(secondUser)
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");

        User updateUser = new User(
                user.getEmail(),
                user.getPassword(),
                user.getName());

        baseUser.updateUserInfo(secondToken, updateUser)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));

        baseUser.deleteUser(secondToken);
    }

    @Test
    @DisplayName("Получение данных неавторизованным пользователем - ошибка 401")
    public void getInfoNotAuthUserFailed() {

        baseUser.getUserInfoNotAuth()
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение email неавторизованным пользователем - ошибка 401")
    public void updateInfoEmailNotAuthFailed() {

        User updateUser = new User(
                "testyou@test.test",
                user.getPassword(),
                user.getName());

        baseUser.updateUserInfoNotAuth(updateUser)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение name неавторизованным пользователем - ошибка 401")
    public void updateInfoNameNoAuthFailed() {

        User updateUser = new User(
                user.getEmail(),
                user.getPassword(),
                "Гектор");

        baseUser.updateUserInfoNotAuth(updateUser)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}