package ru.geekbrains.java;

public interface AuthentificationProvider {
    String getUsernameByLoginAndPassword(String login, String password);
}
