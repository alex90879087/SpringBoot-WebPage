package task2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;


public class ShoppingControllerTest {

    private HttpClient client;
    String session;

    @BeforeEach
    void setUp() {
        this.client = HttpClient.newHttpClient();
    }

    void login() {
        try {
            String login = "user=E";
            HttpRequest request = HttpRequest.newBuilder((new URI("http://localhost:8080/login")))
                    .POST(HttpRequest.BodyPublishers.ofString(login))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            session = response.headers().firstValue("Set-Cookie")
                    .map(header -> header.split(";")[0])
                    .orElse("");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //  login page
    @Test
    void testLogin() throws Exception {
        String login = "user=E";
        HttpRequest request = HttpRequest.newBuilder((new URI("http://localhost:8080/login")))
                .POST(HttpRequest.BodyPublishers.ofString(login))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertNotEquals("Invalid user.", response.body());
    }

    @Test
    void testLoginInvalid() throws Exception {
        try{
            String login = "user=AA";
            HttpRequest request = HttpRequest.newBuilder((new URI("http://localhost:8080/login")))
                    .POST(HttpRequest.BodyPublishers.ofString(login))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            assertEquals("Invalid user.\n", response.body());
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    // cart
    @Test
    void testToAddNewItem() throws URISyntaxException {
        try{
            login();
            HttpRequest request = HttpRequest.newBuilder((new URI("http://localhost:8080/toAddNewItem")))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void testToDeleteItem() throws Exception {
        try{
            login();
            HttpRequest request = HttpRequest.newBuilder((new URI("http://localhost:8080/toDeleteItem")))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void testToUpdateItem() throws Exception {
        try{
            login();
            HttpRequest request = HttpRequest.newBuilder((new URI("http://localhost:8080/toUpdateItem")))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void testToLogOut() throws Exception {
        try{
            login();
            HttpRequest request = HttpRequest.newBuilder((new URI("http://localhost:8080/toLogOut")))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void testAddNewItem() {
        try{
            login();
            String updateData = "name=newitem&price=100";
            HttpRequest updateRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/addNewItem"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(updateData))
                    .build();
            HttpResponse<String> response = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(302, response.statusCode());

            String responseBody = getResponseBody();
            assertTrue(responseBody.contains("newitem"));

        } catch(Exception e) {
            System.out.println(e);
        }
    }
    @Test
    void testDeleteItem() {
        try {
            login();
            String updateData = "checkboxes=orange&checkboxes=banana&checkboxes=apple";
            HttpRequest updateRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/deleteItem"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(updateData))
                    .build();
            HttpResponse<String> response = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(302, response.statusCode());


            String responseBody = getResponseBody();
            assertFalse(responseBody.contains("pear"));

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    // has to restart spring boot to run second since values are modified
    @Test
    void testUpdateNameAndPrice() {
        try {
            login();
            String updateData = "orange=asd&orangeprice=555&banana=&bananaprice=&apple=&appleprice=";
            HttpRequest updateRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/updateItem"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(updateData))
                    .build();
            HttpResponse<String> response = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(302, response.statusCode());


            String responseBody = getResponseBody();
            System.out.println(responseBody);
            assertFalse(responseBody.contains("pear"));
            assertFalse(responseBody.contains("orange"));
            assertTrue(responseBody.contains("asd"));

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    @Test
    void testLogOut() {
        try {
            login();
            HttpRequest updateRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/toLogOut"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            String responseBody = getResponseBody();
            System.out.println(responseBody);
            assertTrue(responseBody.contains("Invalid User Id"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // has to restart spring boot to run second since values are modified
    @Test
    void testUpdateCount() throws Exception {
        try{
            login();
            String updateData = "banana=1&apple=1&asd=11";
            HttpRequest updateRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/updateCount"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", session)
                    .POST(HttpRequest.BodyPublishers.ofString(updateData))
                    .build();
            HttpResponse<String> response = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(302, response.statusCode());

            String responseBody = getResponseBody();
            assertTrue(responseBody.contains("Cart"));
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    String getResponseBody() {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/cart"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", session)
                    .GET()
                    .build();
             return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }




}
