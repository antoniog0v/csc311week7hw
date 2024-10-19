/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.javafxdb_sql_shellcode.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.javafxdb_sql_shellcode.Person;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author MoaathAlrajab
 */
public class ConnDbOps {
    final String MYSQL_SERVER_URL = "jdbc:mysql://villanicsc311server.mysql.database.azure.com/";
    final String DB_URL = "jdbc:mysql://villanicsc311server.mysql.database.azure.com/DBname";
    final String USERNAME = "villaniadmin";
    final String PASSWORD = "farmingdale25!";

    public boolean connectToDatabase() {
        boolean hasRegistredUsers = false;


        //Class.forName("com.mysql.jdbc.Driver");
        try {
            //First, connect to MYSQL server and create the database if not created
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS DBname");
            statement.close();
            conn.close();

            //Second, connect to the database and create the table "users" if cot created
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT( 10 ) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + "name VARCHAR(200) NOT NULL,"
                    + "email VARCHAR(200) NOT NULL UNIQUE,"
                    + "phone VARCHAR(200),"
                    + "address VARCHAR(200),"
                    + "password VARCHAR(200) NOT NULL"
                    + ")";
            statement.executeUpdate(sql);

            //check if we have users in the table users
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }

            statement.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasRegistredUsers;
    }

//Getting users from database so that you can put them in the observable list

    public ObservableList<Person> getUserFromDatabase() {
        ObservableList<Person> personList = FXCollections.observableArrayList();

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT name, email, phone, address, password FROM users";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            //Iterate over the result set and create Person objects, which we can put into the list (all from database)
            while (resultSet.next()) {
                String fullName = resultSet.getString("name");
                String emailAddress = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone");
                String houseAddress = resultSet.getString("address");
                String password = resultSet.getString("password");

                // Create a new Person object and add it to the list
                Person person = new Person(fullName, emailAddress, phoneNumber, houseAddress, password);
                personList.add(person);
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personList;  // Return the list of users
    }

    //Method for deleting user from database

    public void deleteUserFromDatabase(String emailAddress) {

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            //SQL delete query to remove the user with the given email (still based off selection tho)
            String sql = "DELETE FROM users WHERE email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            // Set the email to identify which user to delete
            preparedStatement.setString(1, emailAddress);

            //deletion
            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("User was deleted successfully from the database.");
            } else {
                System.out.println("No user found based on given email.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  void queryUserByName(String name) {


        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                System.out.println("Name: " + name + ", Email: " + email + ", Phone #: " + phone + ", Address: " + address);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Lists all users

    public  void listAllUsers() {



        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM users ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String address = resultSet.getString("address");
                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Phone: " + phone + ", Address: " + address);
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Inserts new user

    public  void insertUser(String name, String email, String phone, String address, String password) {


        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO users (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                System.out.println("A new user was inserted successfully.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Updating user so you can edit them freely. It's edited using the email as a primary key

    public void updateUser(String name, String email, String phone, String address, String password, String firstEmail) {

            try {
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

                //SQL update query to modify the user with the given email
                String sql = "UPDATE users SET name = ?, email = ?, phone = ?, address = ?, password = ? WHERE email = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);

                //Set the values to update
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, phone);
                preparedStatement.setString(4, address);
                preparedStatement.setString(5, password);
                preparedStatement.setString(6, firstEmail);


                //Execute the update
                int row = preparedStatement.executeUpdate();

                if (row > 0) {
                    System.out.println("User information was updated successfully.");
                } else {
                    System.out.println("No user found with the given email.");
                }

                preparedStatement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    

