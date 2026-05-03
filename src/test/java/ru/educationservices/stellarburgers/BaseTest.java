package ru.educationservices.stellarburgers;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    @BeforeEach
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
    }
}