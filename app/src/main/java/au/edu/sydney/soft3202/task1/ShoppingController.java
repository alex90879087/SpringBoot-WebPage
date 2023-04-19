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
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ShoppingController {
    private final SecureRandom randomNumberGenerator = new SecureRandom();
    private final HexFormat hexFormatter = HexFormat.of();
    private DbController dbController = new DbController();

    private final AtomicLong counter = new AtomicLong();
    ShoppingBasket shoppingBasket = new ShoppingBasket();

    Map<String, String> sessions = new HashMap<>();

    String currentUser;

    List<String> users = new ArrayList<>();

    Map<String, ShoppingBasket> baskets = new HashMap<>();

    public ShoppingController() throws SQLException {
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam(value = "user", defaultValue = "") String user) {
        // We are just checking the username, in the real world you would also check their password here
        // or authenticate the user some other way.

        try {
            if (!user.equals("Admin")) {
                user = dbController.getUser(user);
            }
        } catch (SQLException error) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Unable to connect: " + error.getMessage()+ ".\n");
        }

        if (user == null) {
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
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).location(URI.create("/cart")).build();
    }

    @GetMapping("/toAddNewUser")
    public String toAddNewUser() {
        return "addNewUser";
    }

    @GetMapping("/users")
    public String toUsers(Model model) throws SQLException {
        users = dbController.getUsers();
        model.addAttribute("users", users);
        return "users";
    }

    // alphanumeric -> include special character for now
    @PostMapping("addNewUser")
    public String addNewUser(@RequestParam(value = "name") String name, Model model) throws SQLException {
        dbController.addUser(name);
        baskets.put(name, new ShoppingBasket());
        return "redirect:/users";
    }

    //
    @PostMapping("/updateUser")
    public String deleteUser(@RequestParam("userToDelete") List<String> checkBox) {

        for (String each: checkBox) {
            try{
                dbController.removeUser(each);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/users";
    }


    @PostMapping("/updateCount")
    public String updateQuantity(@RequestParam Map<String,String> request) {
        // Iterate through the items in the basket and update their quantities=
        ShoppingBasket basket = baskets.get(currentUser);
        List<Map.Entry<String, Integer>> items = basket.getItems();

        for (Map.Entry<String, Integer> entry : items) {
            String item = entry.getKey();
            Integer quantity = entry.getValue();
            if (request.get(item).length() != 0) {
                try{
                    Integer.valueOf(request.get(item));
                }catch(Exception e) {
                    throw new IllegalArgumentException("New quantity has to be numbers");
                }
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

    @GetMapping("/toAddNewItem")
    public String toAddNewItem() {
        return "newname";
    }
    @GetMapping("/toDeleteItem")
    public String toDeleteItem(Model model) {
        ShoppingBasket basket = baskets.get(currentUser);
        model.addAttribute("basket", basket);
        return "delname";
    }
    @GetMapping("/toUpdateItem")
    public String toUpdateItem(Model model) {
        ShoppingBasket basket = baskets.get(currentUser);
        model.addAttribute("basket", basket);
        return "updatename";
    }
    @GetMapping("/toLogOut")
    public String toLogOut(Model model) {
        // removing session token
        sessions.clear();
        return "index";
    }

    @PostMapping("updateItem")
    public String updateItem(@RequestParam(defaultValue = "") Map<String,String> request) {
        ShoppingBasket basket = baskets.get(currentUser);
        List<Map.Entry<String, Integer>> items = basket.getItems();
        for (Map.Entry<String, Integer> entry : items) {
            String item = entry.getKey().toLowerCase(Locale.ROOT);

            // check and update price first
            String priceKey = item + "price";
            if (request.containsKey(priceKey)){
//                System.out.println("price key is " + priceKey);
                if (request.get(priceKey) != null && request.get(priceKey).length() != 0) {
                    try{
                        basket.updateCost(item, Double.parseDouble(request.get(priceKey)));
                    } catch(Exception e) {
                        throw new IllegalArgumentException("New price has to be numbers");
                    }
                }

                // then update name in case of old name not appearing
                if (request.containsKey(item)) {
//                    System.out.println("item name is " + item);
                    if (request.get(item) != null && request.get(item).length() != 0) {
                        basket.updateName(item, request.get(item));
                    }
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("deleteItem")
    public String deleteItem(@RequestParam("checkboxes") List<String> checkBox) {
        ShoppingBasket basket = baskets.get(currentUser);
        List<String> allItems = basket.getLsOfItems();
        allItems.removeAll(checkBox);
        for (int i = 0; i < allItems.size(); i ++) {
            basket.deleteItem(allItems.get(i));
        }
        return "redirect:/cart";
    }

    @PostMapping("/addNewItem")
    public String addNewItem(@RequestParam(value = "name") String name,
                             @RequestParam(value = "price") String price) {

        ShoppingBasket basket = baskets.get(currentUser);
        if (name == "") throw new IllegalArgumentException("Name cannot be empty");
        if (price == "") throw new IllegalArgumentException("Price cannot be empty");
        if (name == "" && price == "" ) throw new IllegalArgumentException("Name and price cannot be empty");
        try{
            Double.parseDouble(price);
        }catch(Exception e) {
            throw new IllegalArgumentException("Price needs to be number");
        }
        basket.addNewItem(name, Double.parseDouble(price));

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(@CookieValue(value = "session", defaultValue = "") String sessionToken, Model model) {
        if (!sessions.containsKey(sessionToken)) throw new IllegalArgumentException("Invalid User Id");

        if (currentUser.equals("Admin")) {
            try{
                dbController.getUsers();
            } catch (SQLException e) {
                return "error";
            }
            model.addAttribute("users", users);

            return "users";
        }
        // to get current user's cart
        String user = sessions.get(sessionToken);
        ShoppingBasket basket = baskets.get(user);

        model.addAttribute("user", user);
        model.addAttribute("basket", basket);

        return "cart";
    }

    // Exception Handler
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("<div style=\"text-align: center\"><h1>" + exception.getMessage() + "</h1></div>" +
                        "<img src=/image/error.gif style=width:100%; height:100%;/>");}
}
