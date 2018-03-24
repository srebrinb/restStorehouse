package srebrinb.nosql.webstore;

import io.restassured.RestAssured;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import java.io.File;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author sbalabanov
 * https://github.com/rest-assured/rest-assured/wiki/Usage
 */
public class WebSrvVerticleTest {

    public WebSrvVerticleTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        String[] args = null;
        WebSrvVerticle.main(args);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Integer.getInteger("http.port", 8080);
    }

    @AfterClass
    public static void tearDownClass() {
        RestAssured.reset();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class WebSrvVerticle.
     */
    @Test
    public void testMain() {

        get("/api/doc/1").then().body("ok", is(true));
    }

    @Test
    public void testUpload() {
        given().
                multiPart(new File("src/test/resources/demo.pdf")).
        when().
                post("/api/docs").
        then().
                statusCode(200);
    }
}
