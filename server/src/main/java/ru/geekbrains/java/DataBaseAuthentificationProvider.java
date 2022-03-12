package ru.geekbrains.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseAuthentificationProvider implements AuthentificationProvider{

    private class DBUserInfo{
        private String login;
        private String password;
        private String username;

        public DBUserInfo(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    private List<DataBaseAuthentificationProvider.DBUserInfo> users;
    private static Connection connection;
    private static Statement statement;

    @Override
    public void start() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
            statement = connection.createStatement();
            createTable();
            insertUsers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTable() throws SQLException {
        String sql="create table if not exists chatUsers (\n"+
                "id integer primary key autoincrement not null,\n"+
                "login text not null,\n"+
                "password text not null,\n"+
                "username text not null\n"+
                ");";
        statement.executeUpdate(sql);
    }

    public void insertUsers() throws SQLException {
        try(PreparedStatement preparedStatement= connection.prepareStatement("insert into chatUsers (login,password, username) values (?,?,?);")){
            preparedStatement.setString(1,"admin");preparedStatement.setString(2,"admin");preparedStatement.setString(3,"GreatAdmin");
            preparedStatement.addBatch();
            preparedStatement.setString(1,"user1");preparedStatement.setString(2,"1234");preparedStatement.setString(3,"MaybeUser");
            preparedStatement.addBatch();
            preparedStatement.setString(1,"user2");preparedStatement.setString(2,"1111");preparedStatement.setString(3,"TheOnlyUser");
            preparedStatement.addBatch();
            preparedStatement.setString(1,"guest");preparedStatement.setString(2,"0000");preparedStatement.setString(3,"UnknownGuest");
            preparedStatement.addBatch();

            preparedStatement.executeBatch();
        }

    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        if (connection == null){
            System.out.println("null con db");
        }
        System.out.println(connection);
        System.out.println(login +" " + password);
        try(PreparedStatement preparedStatement = connection.prepareStatement("select username from chatUsers where login=? and password=?")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.getString(1);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void changeUsername(String username, String newUsername){
        try(PreparedStatement preparedStatement=
                    connection.prepareStatement("update chatUsers set username =? where id=(select id from chatUsers where username=?)")){
            preparedStatement.setString(1,newUsername);
            preparedStatement.setString(2,username);
            preparedStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if(statement !=  null){
            try {
                dropTable();
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if( connection!= null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void dropTable() throws SQLException {
        statement.execute("drop table chatUsers;");
    }
}
