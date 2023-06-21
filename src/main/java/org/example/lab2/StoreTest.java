package org.example.lab2;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeClass;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class StoreTest {

    final int VARIANT = 17;
    final int SHORT_VARIANT = 7;

    private static final Logger log = Logger.getLogger(UserTest.class);

    private static final String baseURL = "https://petstore.swagger.io/v2/";
    private static final String STORE = "store",
            ORDER = STORE + "/order",
            ORDER_ID = ORDER + "/{orderId}",
            INVENTORY = STORE + "/inventory";

    private final String SHIP_DATE = "2023-05-17T10:01:40.545Z";
    private final String ORDER_STATUS = "placed";

    @BeforeClass
    public void setup(){
        try{
            log("Individual part execution");
            log("Pet Store API test");

            PropertyConfigurator.configure("src/main/resources/log4j.properties");
            RestAssured.baseURI = baseURL;
            RestAssured.defaultParser = Parser.JSON;
            RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
            RestAssured.responseSpecification = new ResponseSpecBuilder().build();
        } catch(Exception e){
            logException(e);
        }
    }

    @Test
    public void verifyCreateOrder(){
        try{
            log("Trying to place order with ID " + SHORT_VARIANT);
            log("Pet ID " + VARIANT);
            log("API URL: " + ORDER);
            Map<String,?> body = Map.of(
                    "id", SHORT_VARIANT,
                    "petId", VARIANT,
                    "shipDate", SHIP_DATE,
                    "status", ORDER_STATUS,
                    "complete", true);
            Response response = given().body(body).post(ORDER);
            response
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .and()
                    .body("id", equalTo(SHORT_VARIANT))
                    .and()
                    .body("petId", equalTo(VARIANT))
                    .and()
                    .body("status", equalTo(ORDER_STATUS));
        }catch(Exception e){
            logException(e);
        }
    }

    @Test(dependsOnMethods = "verifyCreateOrder")
    public void verifyGetOrderAction(){
        try{
            log.info("Trying to get order: " + SHORT_VARIANT);
            log("API URL: " + ORDER_ID);
            given()
                    .pathParam("orderId", SHORT_VARIANT)
                    .get(ORDER_ID)
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .and()
                    .body("petId", equalTo(VARIANT));
        } catch (Exception e){
            logException(e);
        }
    }

    @Test(dependsOnMethods = "verifyCreateOrder", priority = 1)
    public void verifyRemoveOrderAction(){
        try{
            log.info("Trying to remove order: " + SHORT_VARIANT);
            log("API URL: " + ORDER_ID);
            given()
                    .pathParam("orderId", SHORT_VARIANT)
                    .delete(ORDER_ID)
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        }catch(Exception e){
            logException(e);
        }
    }

    @Test()
    public void verifyGetINVENTORYAction(){
        try{
            log.info("Trying to get INVENTORY");
            log("API URL: " + INVENTORY);
            given()
                    .get(INVENTORY)
                    .then()
                    .statusCode(HttpStatus.SC_OK);
        } catch (Exception e){
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
