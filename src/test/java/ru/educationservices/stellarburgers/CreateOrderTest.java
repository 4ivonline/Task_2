package ru.educationservices.stellarburgers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest extends BaseTest {

    private User user;
    private BaseUser baseUser = new BaseUser();
    private BaseOrder baseOrder = new BaseOrder();
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

    @ParameterizedTest
    @MethodSource("correctOrder")
    @DisplayName("Создание заказа - успешно")
    public void createOrderSuccess (List <String> ingredients) {

        Order newOrder = new Order(ingredients);

        baseOrder.createOrderInfo(newOrder, accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    private static Stream<Arguments> correctOrder () {

        return Stream.of(
                Arguments.of(List.of("61c0c5a71d1f82001bdaaa6d")),
                Arguments.of(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6c")),
                Arguments.of(List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa74"))
        );
    }

    @ParameterizedTest
    @MethodSource("emptyOrderFail")
    @DisplayName("Создание заказа - отсутствует значение ингридиента - ошибка: 400")
    public void createEmptyOrderFailed (List <String> ingredients) {

        Order newOrder = new Order(ingredients);

        baseOrder.createOrderInfo(newOrder, accessToken)
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    private static Stream<Arguments> emptyOrderFail () {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of((List<String>) null)
        );
    }

    @ParameterizedTest
    @MethodSource("incorrectOrderFail")
    @DisplayName("Создание заказа - неправильное значение ингридиента - ошибка: 500")
    public void createIncorrectOrderFailed (List <String> ingredients) {

        Order newOrder = new Order(ingredients);

        baseOrder.createOrderInfo(newOrder, accessToken)
                .then()
                .statusCode(500);
    }

    private static Stream<Arguments> incorrectOrderFail () {

        return Stream.of(
                Arguments.of(List.of( "61c0c5a71d1 f82001bdaaa6d")),
                Arguments.of(List.of("61c0c5a71d1-f82001bdaaa6d")),
                Arguments.of(List.of("61c0c5a71d1_f82001bdaaa6d")),
                Arguments.of(List.of( "6@1c0!c5a?7*1d1f&82001$bdaaa6d")),
                Arguments.of(List.of("XXXYYY")),
                Arguments.of(List.of("xxxyyy")),
                Arguments.of(List.of("123456"))
        );
    }

    @Test
    @DisplayName("Создание заказа без авторизации - успешно")
    public void createOrderNoAuthSuccess() {

        Order newOrder = new Order(List.of("61c0c5a71d1f82001bdaaa6d"));

        baseOrder.createOrderInfoNoAuth(newOrder)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }
}