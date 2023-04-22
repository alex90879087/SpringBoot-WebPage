package DatabasController;

import au.edu.sydney.soft3202.task1.ShoppingBasket;

import javax.xml.transform.Result;
import java.sql.*;

public class BasketDB {

    private static final String dbName = "basket.db";
    private Connection connection;

    public BasketDB() throws SQLException {
        this.connect();
        this.dropBasketSchema();
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
                        "user TEXT PRIMARY KEY NOT NULL," +
                        "name TEXT NOT NULL," +
                        "price REAL NOT NULL," +
                        "quantity INT NOT　NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException("Failed to create basket table");
        }
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
        String sql = "INSERT INTO BASKETS VALUES( (?), (?), (?), 5)";
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            prepStatement.setString(2, name);
            prepStatement.setString(3, String.valueOf(price));
            prepStatement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // price and quantity
    public int[] getItemData(String user, String name) {
        int[] toReturn = new int[2];
        String sqlPrice = "SELECT PRICE FROM BASKETS WHERE USER = (?) AND NAME = (?)";
        String sqlQuantity = "SELECT QUANTITY FROM BASKETS WHERE USER = (?) AND NAME = (?)" ;
        String sqlPriceAndQuantity = "SELECT PRICE, QUANTITY FROM BASKETS WHERE USER = (?) AND NAME = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sqlPriceAndQuantity)) {
            prepStatement.setString(1, user);
            prepStatement.setString(2, name);
            ResultSet temp = prepStatement.executeQuery();
            toReturn[0] = Integer.parseInt(temp.getString("price"));
            toReturn[1] =Integer.parseInt(temp.getString("quantity"));
        } catch (SQLException e){
            e.printStackTrace();
        }

        return toReturn;
    }

    public void deleteSpecificItem(String user, String name) {
        String sql = "DELETE FROM BASKETS WHERE USER = (?) AND name = (?)";

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

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            int rs = prepStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updatePrice(String user, String name, String newPrice) {
        String sql = "UPDATE BASKETS SET PRICE = (?) WHERE user = (?) AND name = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, newPrice);
            prepStatement.setString(2, user);
            prepStatement.setString(3, name);
            int rs = prepStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void updateQuantity(String user, String name, String newQuantity) {
        String sql = "UPDATE BASKETS SET QUANTITY = (?) WHERE user = (?) AND name = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, newQuantity);
            prepStatement.setString(2, user);
            prepStatement.setString(3, name);
            int rs = prepStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateName(String user, String name, String newName) {
        String sql = "UPDATE BASKETS SET name = (?) WHERE user = (?) AND name = (?)";

        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, newName);
            prepStatement.setString(2, user);
            prepStatement.setString(3, name);
            int rs = prepStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void getItems(ShoppingBasket basket, String user) {
        String sql = "SELECT *　FROM BASKETS WHERE user = (?)";
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, user);
            ResultSet rs = prepStatement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                Double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                basket.update(name, price, quantity);

            }

        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws SQLException {
        BasketDB asd = new BasketDB();
        asd.addItem("A", "testItem", 10);
        int[] qq = asd.getItemData("A", "testItem");
        System.out.println(qq[0]);
        System.out.println(qq[1]);
        asd.updateName("A", "testItem", "newName");
        qq = asd.getItemData("A", "newName");
        System.out.println(qq[0]);
        System.out.println(qq[1]);
        asd.updateQuantity("A", "newName","100");
        qq = asd.getItemData("A", "newName");
        System.out.println(qq[0]);
        System.out.println(qq[1]);

    }
}
