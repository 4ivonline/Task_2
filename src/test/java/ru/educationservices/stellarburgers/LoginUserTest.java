package ru.educationservices.stellarburgers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest extends BaseTest {

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
    @DisplayName("Логин пользователя - успешно")
    public void loginUserSuccess() {

        baseUser.loginUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @ParameterizedTest
    @MethodSource("failLoginUser")
    @DisplayName("Логин пользователя - неверные данные логина/пароля - ошибка: 401")
    public void loginWithFailLoginOrPassFailed (String email, String password) {
        user = new User(email, password);

        baseUser.loginUser(user)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    private static Stream<Arguments> failLoginUser () {

        return Stream.of(
                Arguments.of("usertest002@test.test", "1234"),
                Arguments.of("usertest001@test.test", "12345"),
                Arguments.of("usertest001@test.test", null),
                Arguments.of(null, "usertest001@test.test"),
                Arguments.of(null, null)
        );
    }
}