package au.edu.sydney.soft3202.task1;

import DatabasController.BasketDB;

import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
* Container for items to be purchased
*/
public class ShoppingBasket {

    HashMap<String, Integer> items;
    HashMap<String, Double> values;
    String user;
//    BasketDB db = new BasketDB();
    String[] names = {"apple", "orange", "pear", "banana"};

    /**
    * Creates a new, empty ShoppingBasket object
    */
    public ShoppingBasket(String user) throws SQLException {
        this.user = user;
        this.items = new HashMap<>();
        this.values = new HashMap<>();
    }

//    public static void main(String[] args) throws SQLException {
//        ShoppingBasket sut = new ShoppingBasket("A");
//        sut.addNewItem("apple", 5);
//        sut.addItem("apple", 429496730);
//        System.out.println(sut.getValue());
//    }

    public void initialise() {
        for (String name: names) {
            this.items.put(name, 0);
        }

        this.values.put("apple", 2.5);
        this.values.put("orange", 1.25);
        this.values.put("pear", 3.00);
        this.values.put("banana", 4.95);

        // 0 count when adding new items
        this.addNewItem("apple", 2.5);
        this.addNewItem("orange", 1.25);
        this.addNewItem("pear", 3.00);
        this.addNewItem("banana", 4.95);
    }

    public void update(String item, double price, int count) {

//        if (this.items.containsKey(item)) {
        this.items.put(item, count);
        this.values.put(item, price);
        return;
//        }
    }
//        throw new IllegalArgumentException("Error when updating baskets value and quantity");



    public void addItem(String item, int count) throws IllegalArgumentException {
        if (item == null) throw new IllegalArgumentException("Item is invalid");
        String stringItem = item.toLowerCase();

        if (!this.items.containsKey(stringItem)) throw new IllegalArgumentException("Item " + stringItem + " is not present.");
        if (count < 1) throw new IllegalArgumentException("Item " + item + " has invalid count.");

        Integer itemVal = this.items.get(stringItem);
        if (itemVal == Integer.MAX_VALUE) throw new IllegalArgumentException("Item " + item + " has reached maximum count.");

        this.items.put(stringItem, itemVal + count);
//        this.db.updateQuantity(this.user, item, String.valueOf(count));
    }

    /**
    * Removes an item from the shopping basket, based on a case-insensitive but otherwise exact match.
     *
     * @param item  The item to be removed.
     * @param count The count of the item to be added. Must be 1 or more.
     * @return False if the item was not found in the basket, or if the count was higher than the amount of this item currently present, otherwise true.
     * @throws IllegalArgumentException If any parameter requirements are breached.
    */
    public boolean removeItem(String item, int count) throws IllegalArgumentException {
        if (item == null) throw new IllegalArgumentException("Item is invalid");
        String stringItem = item.toLowerCase();

        if (!this.items.containsKey(stringItem)) return false;
        if (count < 1) throw new IllegalArgumentException(count + " is invalid count.");

        Integer itemVal = this.items.get(stringItem);

        Integer newVal = itemVal - count;
        if (newVal < 0) return false;
        this.items.put(stringItem, newVal);
//        this.db.updateQuantity(this.user, stringItem, String.valueOf(newVal));
//        this.db.deleteSpecificItem(item, String.valueOf(count));
        return true;
    }

    /**
    * Gets the contents of the ShoppingBasket.
    *
    * @return A list of items and counts of each item in the basket. This list is a copy and any modifications will not modify the existing basket.
    */
    public List<Entry<String, Integer>> getItems() {
        ArrayList<Entry<String, Integer>> originalItems = new ArrayList<Entry<String, Integer>>(this.items.entrySet());
        ArrayList<Entry<String, Integer>> copyItems = new ArrayList<Entry<String, Integer>>();

        int index = 0;

        for(Entry<String,Integer> entry: originalItems){
            copyItems.add(index, Map.entry(entry.getKey(), entry.getValue()));
            index++;
        }

        return copyItems;
    }


    /**
    * Gets the current dollar value of the ShoppingBasket based on the following values: Apples: $2.50, Oranges: $1.25, Pears: $3.00, Bananas: $4.95
    *
    * @return Null if the ShoppingBasket is empty, otherwise the total dollar value.
    */
    public Double getValue() {
        Double val = 0.0;

        for (String name: names) {
           val += this.values.get(name) * this.items.get(name);
        }

        if (val == 0.0) return null;
        return val;
    }

    /**
    * Empties the ShoppingBasket, removing all items.
    */
    public void clear() {
        for (String name: names) {
            this.items.put(name, 0);
        }
    }

    // new method
    public List<Entry<String, Double>> getItemsValues() {
        return new ArrayList<>(this.values.entrySet());
    }

    // need to clarify if it is case-sensitive
    public void addNewItem(String item, double values) {

        if (values < 0) throw new IllegalArgumentException("Item " + item + " cannot have negative price!");
        item = item.toLowerCase(Locale.ROOT);

        if (this.items.containsKey(item)) {
            this.items.put(item, 0);
        }
        this.items.put(item, 0);
        this.values.put(item, values);
//        this.db.addItem(user, item, values);
    }

    public void deleteItem(String item) throws IllegalArgumentException {
        if (item == null) throw new IllegalArgumentException("Item is invalid");
        String stringItem = item.toLowerCase();
        this.values.remove(stringItem);
        this.items.remove(stringItem);
//        this.db.deleteSpecificItem(user, item);
    }

    public void updateName(String oldName, String newName) {
        if (oldName == null || oldName.length() == 0 ||
            !values.containsKey(oldName) || newName == null || newName.length() == 0) throw new IllegalArgumentException("Item is invalid");

        int count = items.get(oldName);
        items.remove(oldName);
        items.put(newName, count);

        double value = values.get(oldName);
        values.remove(oldName);
        values.put(newName, value);
//        this.db.updateName(user, oldName, newName);
    }

    public void updateCost(String item, double newCost) {
        if (item == null || item.length() == 0 || !values.containsKey(item) || newCost <= 0) throw new IllegalArgumentException("Item is invalid");

        values.remove(item);
        values.put(item, newCost);
//        this.db.updatePrice(user, item, String.valueOf(newCost));
    }

    public List<String> getLsOfItems() {
        return new ArrayList<>(this.items.keySet());
    }

}
