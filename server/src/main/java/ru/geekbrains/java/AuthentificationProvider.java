package ru.geekbrains.java;

public interface AuthentificationProvider {
    void start();
    void stop();
    String getUsernameByLoginAndPassword(String login, String password);
    void changeUsername(String username, String newUsername);
}
