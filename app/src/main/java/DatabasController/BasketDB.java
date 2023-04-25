package DatabasController;

import au.edu.sydney.soft3202.task1.ShoppingBasket;

import java.sql.*;
import java.util.Map;

public class BasketDB {

    private static final String dbName = "basket.db";
    private Connection connection;

    public BasketDB() throws SQLException {
        this.connect();
//        this.dropBasketSchema();
        this.createBasketSchema();
    }

    private void connect() throws SQLException {
        try  {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);

        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to db");
            throw new SQLException("Please try connecting to database later");
        }
    }

    private void createBasketSchema() throws SQLException {
        String sql =
                "CREATE TABLE IF NOT EXISTS BASKETS (" +
                        "user TEXT NOT NULL," +
                        "item TEXT NOT NULL," +
                        "price REAL NOT NULL," +
                        "quantity INT NOT　NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException("Failed to create basket table");
        }
    }

    public void initialise(String user) {
        this.addItem(user, "apple", 2.5);
        this.addItem(user, "orange", 1.25);
        this.addItem(user, "pear", 3.00);
        this.addItem(user, "banana", 4.95);
        this.getItems(user);
    }

    private void dropBasketSchema() throws SQLException {
        String sql = "DROP TABLE IF EXISTS BASKETS";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new SQLException("Failed to drop basket schema");
        }
    }

    public void addItem(String user, String name, double price) {
        String sql = "INSERT INTO BASKETS (user, item, price, quantity) VALUES((?), (?), (?), 0)";
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            prepStatement.setString(2, name);
            prepStatement.setString(3, String.valueOf(price));
            System.out.println(prepStatement.toString());

            prepStatement.executeUpdate();
        } catch (SQLException e){
            throw new IllegalArgumentException("Item " + name + " added already!");
        }
    }

    // price and quantity
    public double[] getItemData(String user, String name) {
        double[] toReturn = new double[2];
        String sqlPrice = "SELECT PRICE FROM BASKETS WHERE USER = (?) AND ITEM = (?)";
        String sqlQuantity = "SELECT QUANTITY FROM BASKETS WHERE USER = (?) AND ITEM = (?)" ;
        String sqlPriceAndQuantity = "SELECT PRICE, QUANTITY FROM BASKETS WHERE USER = (?) AND ITEM = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sqlPriceAndQuantity)) {
            prepStatement.setString(1, user);
            prepStatement.setString(2, name);
            ResultSet temp = prepStatement.executeQuery();
            toReturn[0] = Double.parseDouble(temp.getString("price"));
            toReturn[1] = Double.parseDouble(temp.getString("quantity"));
        } catch (SQLException e){
            e.printStackTrace();
        }

        return toReturn;
    }

    public void deleteSpecificItem(String user, String name) {
        String sql = "DELETE FROM BASKETS WHERE USER = (?) AND item = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            prepStatement.setString(2, name);
            int rs = prepStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteItems(String user) {
        String sql = "DELETE FROM BASKETS WHERE USER = (?)";
        System.out.println("user +" + user);
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            int rs = prepStatement.executeUpdate();
            System.out.println(rs);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updatePrice(String user, String name, String newPrice) {
        String sql = "UPDATE BASKETS SET PRICE = (?) WHERE user = (?) AND item = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, newPrice);
            prepStatement.setString(2, user);
            prepStatement.setString(3, name);
            System.out.println(prepStatement.toString());

            int rs = prepStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void updateQuantity(String user, String name, String newQuantity) {
        String sql = "UPDATE BASKETS SET QUANTITY = (?) WHERE user = (?) AND item = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, newQuantity);
            prepStatement.setString(2, user);
            prepStatement.setString(3, name);
            System.out.println(prepStatement.toString());

            int rs = prepStatement.executeUpdate();
            this.getItems(user);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateName(String user, String name, String newName) {
        String sql = "UPDATE BASKETS SET item = (?) WHERE user = (?) AND item = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, newName);
            prepStatement.setString(2, user);
            prepStatement.setString(3, name);
            System.out.println(prepStatement.toString());
            int rs = prepStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void getItems(ShoppingBasket basket, String user) {
        String sql = "SELECT * FROM BASKETS WHERE user = (?)";
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            ResultSet rs = prepStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("item");
                Double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                basket.update(name, price, quantity);

            }

        } catch (SQLException e){
            e.printStackTrace();
        }

    }
    public void getEverything() {
        String sql = "SELECT * FROM BASKETS";
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            ResultSet rs = prepStatement.executeQuery();

            while (rs.next()) {
                String user = rs.getString("user");
                String name = rs.getString("item");
                Double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                System.out.println("User " + user);
                System.out.println(name);
                System.out.println(price);
                System.out.println(quantity);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void getItems(String user) {
        String sql = "SELECT * FROM BASKETS WHERE user = ?";
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            ResultSet rs = prepStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("item");
                Double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                System.out.println(name);
                System.out.println(price);
                System.out.println(quantity);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws SQLException {
        BasketDB asd = new BasketDB();
//        asd.addItem("A", "testItem", 10);
//        double[] qq = asd.getItemData("A", "testItem");
//        asd.getItems("A");
//        asd.updateName("A", "testItem", "newName");
//        asd.updateQuantity("A", "newName", String.valueOf(10));
//        asd.getItems("A");
        asd.getEverything();


    }
}
