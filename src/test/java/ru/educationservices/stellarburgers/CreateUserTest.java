package ru.educationservices.stellarburgers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest extends BaseTest {

    private User user;
    private BaseUser baseUser = new BaseUser();
    private String accessToken;

    //Удаление пользователя
    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            baseUser.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание пользователя - успешно")
    public void createUserSuccess() {
        user = new User(
                "usertest00@test.test",
                "1234",
                "Ахилес");

        accessToken = baseUser.createUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя - пользователь с такими данными уже существует - ошибка: 403")
    public void createDuplicateUserFail() {
        user = new User(
                "usertest00@test.test",
                "1234",
                "Ахилес");

        accessToken = baseUser.createUser(user)
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");

        User duplicateUser = new User(user.getEmail(), user.getPassword(), user.getName());
        baseUser.createUser(duplicateUser)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @ParameterizedTest
    @MethodSource("failCreateUser")
    @DisplayName("Создание пользователя - отсутствие обязательных полей - ошибка: 403")
    public void createUserNoRequiredFieldsFail(String email, String password, String name) {
        user = new User(email, password,  name);

        baseUser.createUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    private static Stream<Arguments> failCreateUser () {

        return Stream.of(
                Arguments.of("usertest111@test.test", "1234", null),
                Arguments.of("usertest111@test.test", null, "Mio"),
                Arguments.of(null ,"1234", "Mio"),
                Arguments.of(null ,null, null)
        );
    }
}