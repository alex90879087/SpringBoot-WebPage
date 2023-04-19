package au.edu.sydney.soft3202.task1;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbController {

    private static final String dbName = "fruitbasket.db";
    private Connection connection;

    public DbController() throws SQLException {
        this.connect();
        this.dropUserSchema();
        this.createUserSchema();
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

    private void createUserSchema() throws SQLException {
        String sql =
                "CREATE TABLE IF NOT EXISTS users (user TEXT PRIMARY KEY NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e){
            throw new SQLException("Failed to create user table");
        }
    }

    // or maybe delete when exit (be careful with this one as it only deletes file jvm is closed)
    private void dropUserSchema() throws SQLException {
        String sql = "DROP TABLE IF EXISTS users";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new SQLException("Failed to drop user schema");
        }
    }

    public void addUser(String name) throws SQLException {
        String sql = "INSERT INTO users (user) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(String name) throws SQLException {
        String sql = "DELETE FROM users where user = (?)";
        try (PreparedStatement prepStatement = connection.prepareStatement(sql)) {
            prepStatement.setString(1, name);
            prepStatement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<String> getUsers() throws SQLException {
        String sql = "SELECT user FROM users";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> users = new ArrayList<String>();

        while (resultSet.next()) {
            String user = resultSet.getString("user");
            users.add(user);
        }
        return users;
    }

    public String getUser(String name) throws SQLException {
        String sql = "SELECT user FROM users WHERE user = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String user = resultSet.getString("user");
            return user;
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        DbController asd = new DbController();
        asd.dropUserSchema();
        asd.createUserSchema();
        asd.addUser("WW");
        System.out.println(asd.getUser("WW"));
        asd.removeUser("WW");
        System.out.println(asd.getUser("WW"));

    }



}
