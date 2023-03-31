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

    String currentUser;

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
        currentUser = user;

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
    public String updateQuantity(@RequestParam Map<String,String> request) {        // Iterate through the items in the basket and update their quantities
        ShoppingBasket basket = baskets.get(currentUser);
        for (Map.Entry<String, Integer> entry : basket.getItems()) {
            String item = entry.getKey();
            Integer quantity = entry.getValue();
            if (request.get(item).length() != 0) {
                System.out.println(item);
                Integer newQuantity = Integer.valueOf(request.get(item));
                if (newQuantity == 0) basket.removeItem(item, quantity);
                else {
                    if (quantity >= 1) {
                        basket.removeItem(item, quantity);
                    }
                    basket.addItem(item, newQuantity);
                }
            }
        }

        return "redirect:/cart";
    }

    //@RequestParam(value = "coop", defaultValue = "ChookTown") String coopName,
    //            // @RequestParams here are being used for both query strings and form data,
    //            // for different solution can also have a look at
    //            // https://spring.io/guides/gs/handling-form-submission/
    //            @RequestParam(value = "name") String name,
    //            @RequestParam(value = "powerlevel") int powerLevel,
    //            @RequestParam(value = "favfood") String fav,
    //            @RequestParam(value = "imgpath") String imgPath

    @PostMapping("addItem")
    public String addItem(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "quantity") int quantity
    ) {

        return null;
    }

    @GetMapping("/cart")
    public String cart(@CookieValue(value = "session", defaultValue = "") String sessionToken, Model model) {
        if (!sessions.containsKey(sessionToken)) {
            return "Unauthorised";
        }
        // to get each user's cart
        String user = sessions.get(sessionToken);
        ShoppingBasket basket = baskets.get(user);
        System.out.println(basket.getValue());
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
