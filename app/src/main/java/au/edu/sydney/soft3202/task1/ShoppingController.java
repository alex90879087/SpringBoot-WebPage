package au.edu.sydney.soft3202.task1;

import DatabasController.BasketDB;
import DatabasController.DbController;
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
import java.util.regex.Pattern;

@Controller
public class ShoppingController {
    private final SecureRandom randomNumberGenerator = new SecureRandom();
    private final HexFormat hexFormatter = HexFormat.of();
    private DbController dbController = new DbController();
    private BasketDB basketDbController = new BasketDB();

    private final AtomicLong counter = new AtomicLong();

    Map<String, String> sessions = new HashMap<>();

    String currentUser;

    List<String> users;

    Map<String, ShoppingBasket> baskets = new HashMap<>();

    public ShoppingController() throws SQLException {
        this.currentUser = null;
        this.users = dbController.getUsers();
        System.out.println(Arrays.toString(users.toArray()));
        for (String each : users) {
            ShoppingBasket temp = new ShoppingBasket(each);
            basketDbController.getItems(temp, each);
            basketDbController.getEverything();
            baskets.put(each, temp);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam(value = "user", defaultValue = "") String user) throws SQLException {
        // We are just checking the username, in the real world you would also check their password here
        // or authenticate the user some other way.
        if (user.equals("Admin")) currentUser = "Admin";
        if (!user.equals("Admin")) {
            for (String each: users) {
                if (each.equals(user)) this.currentUser = user;
            }
        }

        if (currentUser == null) {
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



        // Redirect to the cart page, with the session-cookie-setting headers.
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).location(URI.create("/cart")).build();
    }

    @GetMapping("/toAddNewUser")
    public String toAddNewUser() {
        if (!currentUser.equals("Admin")) throw new IllegalArgumentException("Invalid User Id");

        return "addNewUser";
    }

    @GetMapping("/users")
    public String toUsers(Model model) throws SQLException {
        if (!currentUser.equals("Admin")) throw new IllegalArgumentException("Invalid User Id");
        users = dbController.getUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/toAddNewItem")
    public String toAddNewItem() {
        if (currentUser == null) throw new IllegalArgumentException("Invalid User Id");

        return "newname";
    }
    @GetMapping("/toDeleteItem")
    public String toDeleteItem(Model model) {
        if (currentUser == null) throw new IllegalArgumentException("Invalid User Id");
        ShoppingBasket basket = baskets.get(currentUser);
        model.addAttribute("basket", basket);
        return "delname";
    }
    @GetMapping("/toUpdateItem")
    public String toUpdateItem(Model model) {
        if (currentUser == null) throw new IllegalArgumentException("Invalid User Id");

        ShoppingBasket basket = baskets.get(currentUser);
        model.addAttribute("basket", basket);
        return "updatename";
    }
    @GetMapping("/toLogOut")
    public String toLogOut(Model model, @CookieValue(value = "session", defaultValue = "") String sessionToken) {
        // removing session token
        currentUser = null;
        sessions.remove(sessionToken);
        return "index";
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

    // alphanumeric -> include special character for now
    @PostMapping("/addNewUser")
    public String addNewUser(@RequestParam(value = "name") String name, Model model) throws SQLException {
        // pattern of a-z 0-9
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        boolean detected = p.matcher(name).find();

        if (detected) throw new IllegalArgumentException("Only alphanumeric is allowed");
        if (name.equalsIgnoreCase("admin")) throw new IllegalArgumentException("Cannot add another Admin");
        if (users.contains(name)) throw new IllegalArgumentException("Name existed");


        ShoppingBasket temp = new ShoppingBasket(name);
        temp.initialise(); // adding default items
        baskets.put(name, temp);

        dbController.addUser(name);
        basketDbController.initialise(name);

        return "redirect:/users";
    }


    @PostMapping("/updateUser")
    public String deleteUser(@RequestParam(value = "userToDelete", required = false) List<String> checkBox) {

        if (checkBox != null) {
            for (String each : checkBox) {
                try {
                    dbController.removeUser(each);
                    basketDbController.deleteItems(each);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return "redirect:/users";
    }

    // count
    @PostMapping("/updateCount")
    public String updateQuantity(@RequestParam Map<String,String> request) {
        // Iterate through the items in the basket and update their quantities
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
                basketDbController.updateQuantity(this.currentUser, item, String.valueOf(newQuantity));
            }
        }
        return "redirect:/cart";
    }

    // name and price
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
                        basketDbController.updatePrice(this.currentUser, item, String.valueOf(request.get(priceKey)));
                    } catch(Exception e) {
                        throw new IllegalArgumentException("New price has to be numbers");
                    }
                }
                // then update name in case of old name not appearing
                if (request.containsKey(item)) {
//                    System.out.println("item name is " + item);
                    if (request.get(item) != null && request.get(item).length() != 0) {
                        basket.updateName(item, request.get(item));
                        basketDbController.updateName(this.currentUser, item, request.get(item));
                    }
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("deleteItem")
    public String deleteItem(@RequestParam("checkboxes") List<String> checkBox) {
        System.out.println(Arrays.toString(checkBox.toArray()));
        ShoppingBasket basket = baskets.get(currentUser);
        List<String> allItems = basket.getLsOfItems();
        allItems.removeAll(checkBox);
        for (int i = 0; i < allItems.size(); i ++) {
            basket.deleteItem(allItems.get(i));
            this.basketDbController.deleteSpecificItem(this.currentUser, allItems.get(i));
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
        this.basketDbController.addItem(this.currentUser, name, Double.parseDouble(price));

        return "redirect:/cart";
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
