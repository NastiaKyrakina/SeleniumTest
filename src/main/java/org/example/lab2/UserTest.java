package org.example.lab2;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserTest {
    private static final String baseURL = "https://petstore.swagger.io/v2/";
    private static final String USER = "/user",
            USER_USERNAME = USER + "/{username}",
            USER_LOGIN = USER + "/login",
            USER_LOGOUT = USER + "/logout";
    private String username;
    private String firstName;

    private static final Logger log = Logger.getLogger(UserTest.class);
    @BeforeClass
    public void setup(){
        try{
            log("Common part testing");
            log("Test USER API");

            // Configuration for logging
            PropertyConfigurator.configure("src/main/resources/log4j.properties");

            // Configuration for RestAssured class
            RestAssured.baseURI = baseURL;
            RestAssured.defaultParser = Parser.JSON;
            RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
            RestAssured.responseSpecification = new ResponseSpecBuilder().build();
        } catch(Exception e){
            logException(e);
        }
    }
    @Test
    public void verifyLoginAction(){
        try{
            log.info("Trying to login with credentials AnastasiaKurakina / 121m-22-1.17");
            log("API URL: " + USER_LOGIN);
            Map<String,?> body = Map.of(
                    "username","AnastasiaKurakina",
                    "password","121m-22-1.17"
            );
            Response response = given().body(body).get(USER_LOGIN);
            response.then().statusCode(HttpStatus.SC_OK);
            RestAssured.requestSpecification.sessionId(response.jsonPath()
                    .get("message")
                    .toString()
                    .replaceAll("[^0-9]",""));
        }catch(Exception e){
            logException(e);
        }
    }
    @Test(dependsOnMethods = "verifyLoginAction")
    public void verifyCreateAction(){
        try{
            log("Trying to sign in a new user");
            log("API URL: " + USER);
            username = Faker.instance().name().username();
            firstName = Faker.instance().backToTheFuture().character();

            log("New user: " + username + " / " + firstName);

            Map<String,?> body = Map.of(
                    "username", username,
                    "firstName", firstName,
                    "lastName", Faker.instance().backToTheFuture().character(),
                    "email", Faker.instance().internet().emailAddress(),
                    "password", Faker.instance().internet().password(),
                    "phone", Faker.instance().phoneNumber().phoneNumber(),
                    "userStatus",Integer.valueOf("1")
            );
            given().body(body)
                    .post(USER)
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        }catch(Exception e){
            logException(e);
        }
    }
    @Test(dependsOnMethods = "verifyCreateAction")
    public void verifyGetAction(){
        try{
            log("Trying to get created user data");
            log("API URL: " + USER_USERNAME);

            given().pathParam("username",username)
                    .get(USER_USERNAME)
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .and()
                    .body("firstName", equalTo(firstName));
        }catch(Exception e){
            logException(e);
        }
    }
    @Test(dependsOnMethods = "verifyLoginAction", priority = 1)
    public void verifyLogoutAction(){
        try{
            log.info("Trying to logout");
            log("API URL: " + USER_LOGOUT);

            given().get(USER_LOGOUT)
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        }catch(Exception e){
            logException(e);
        }
    }

    void logException(Exception e) {
        log.error(e.getMessage());
    }

    static void log(String text) {
        log.info(text);
    }
}