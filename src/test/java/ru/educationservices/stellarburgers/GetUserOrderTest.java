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

import static org.hamcrest.Matchers.*;

public class GetUserOrderTest extends BaseTest {

    private User user;
    private BaseUser baseUser = new BaseUser();
    private BaseOrder baseOrder = new BaseOrder();
    private String accessToken;
    private List<String> ingredients;

    @BeforeEach
    public void setUpUser() {

        user = new User(
                "usertest001@test.test",
                "1234",
                "Ахилес");

        //Создание пользователя
        baseUser.createUser(user)
                .then()
                .statusCode(200);

        //Логин пользователя
        accessToken = baseUser.loginUser(user)
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");

        //Создание заказа
        ingredients = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6c");
        Order newOrder = new Order(ingredients);

        baseOrder.createOrderInfo(newOrder, accessToken)
                .then()
                .statusCode(200);
    }

    //Удаление пользователя
    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            baseUser.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов пользователя - успешно")
    public void getOrdersUserSuccess() {

        baseOrder.getOrdersUser(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", not(empty()))
                .body("orders[0].ingredients", hasItems(
                        ingredients.get(0),
                        ingredients.get(1)
                ))
                .body("orders[0]._id", notNullValue())
                .body("orders[0].status", notNullValue())
                .body("orders[0].number", notNullValue())
                .body("orders[0].createdAt", notNullValue())
                .body("orders[0].updatedAt", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации - ошибка 401")
    public void getOrdersUserNoAuthFailed() {

        baseOrder.getOrdersUserNoAuth()
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}