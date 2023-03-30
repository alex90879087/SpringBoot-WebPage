package au.edu.sydney.soft3202.task1;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ShoppingController {
    private final SecureRandom randomNumberGenerator = new SecureRandom();
    private final HexFormat hexFormatter = HexFormat.of();

    private final AtomicLong counter = new AtomicLong();
    ShoppingBasket shoppingBasket = new ShoppingBasket();

    Map<String, String> sessions = new HashMap<>();

    String[] users = {"A", "B", "C", "D", "E"};

    Map<String, ShoppingBasket> baskets = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam(value = "user", defaultValue = "") String user) {

        // We are just checking the username, in the real world you would also check their password here
        // or authenticate the user some other way.
        if (!Arrays.asList(users).contains(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user.\n");
        }

        // Generate the session token.
        byte[] sessionTokenBytes = new byte[16];
        randomNumberGenerator.nextBytes(sessionTokenBytes);
        String sessionToken = hexFormatter.formatHex(sessionTokenBytes);

        // Store the association of the session token with the user.
        sessions.put(sessionToken, user);

        // Create HTTP headers including the instruction for the browser to store the session token in a cookie.
        String setCookieHeaderValue = String.format("session=%s; Path=/; HttpOnly; SameSite=Strict;", sessionToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", setCookieHeaderValue);

        if (!baskets.containsKey(user)) baskets.put(user, new ShoppingBasket());

        // Redirect to the cart page, with the session-cookie-setting headers.
        System.out.println(headers);
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).location(URI.create("/cart")).build();
    }

    @PostMapping("/updateCount")
    public ResponseEntity<String> updateCount(@ModelAttribute ShoppingBasket basket, @RequestParam Map<String,Integer> request) {        // Iterate through the items in the basket and update their quantities
        for (Map.Entry<String, Integer> entry : basket.getItems()) {
            String itemName = entry.getKey();
            System.out.println(itemName);
            System.out.println(request.get(itemName));
//            Integer newQuantity = Integer.valueOf(request.getParameter(itemName));
//            basket.addItem(itemName, newQuantity);
        }
        System.out.println(request);
        // Return the updated cart page
        return ResponseEntity.status(HttpStatus.OK).location(URI.create("/cart")).build();
    }

    @GetMapping("/cart")
    public String cart(@CookieValue(value = "session", defaultValue = "") String sessionToken, Model model) {
        if (!sessions.containsKey(sessionToken)) {
            return "Unauthorised";
        }

        // to get each user's cart
        String user = sessions.get(sessionToken);
        ShoppingBasket basket = baskets.get(user);

        model.addAttribute("user", user);
        model.addAttribute("basket", basket);

        return "cart";
    }

    @GetMapping("/counter")
    public ResponseEntity<String> counter() {
        counter.incrementAndGet();
        return ResponseEntity.status(HttpStatus.OK).body("[" + counter + "]");
    }

    @GetMapping("/cost")
    public ResponseEntity<String> cost() {
        return ResponseEntity.status(HttpStatus.OK).body(
            shoppingBasket.getValue() == null ? "0" : shoppingBasket.getValue().toString()
        );
    }

    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(name="name", required=false, defaultValue="World") String name,
        Model model
    ) {
        model.addAttribute("name", name);
        return "greeting";
    }

}
