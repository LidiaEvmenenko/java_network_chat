package ru.geekbrains.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthentificationProvider implements AuthentificationProvider{
    private class UserInfo{
        private String login;
        private String password;
        private String username;

        public UserInfo(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    private List<UserInfo> users;

    public InMemoryAuthentificationProvider() {
        this.users = new ArrayList<>(Arrays.asList(
                new UserInfo("user@gmail.com", "1234", "User"),
                new UserInfo("bob@gmail.com", "1234", "bob"),
                new UserInfo("daniel@gmail.com", "1234", "daniel")
        ));
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for(UserInfo u: users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.username;
            }
        }
        return null;
    }
}

