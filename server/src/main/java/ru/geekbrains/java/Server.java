package ru.geekbrains.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private AuthentificationProvider authentificationProvider;
    private List<ClientHandler> clients;

    public AuthentificationProvider getAuthentificationProvider() {
        return authentificationProvider;
    }

    public Server(){ //запуск сервера, создание списка для добавления туда клиентов, запуск процесса добавления клиентов
        try {
            //this.authentificationProvider= new InMemoryAuthentificationProvider();
            authentificationProvider= new DataBaseAuthentificationProvider();
            authentificationProvider.start();
            this.clients= new ArrayList<>();
            ServerSocket serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен. Ожидаем подключение клиентов..");
            int clientsCount=0;
            while (true) {
                Socket socket = serverSocket.accept();
                clientsCount++;
                System.out.println("Клиент №"+clientsCount +" подключился");
                ClientHandler c =new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(authentificationProvider != null) {
                authentificationProvider.stop();
            }
        }
    }

    public synchronized void subscribe(ClientHandler c){ //проверка имени клиента,
        // в случае уникальности клиент добавляется в список активных клиентов сервера - участники чата
        broadcastMessage("К чату подключился пользователь "+c.getName());
        clients.add(c);
        broadcastClientList();
    }

    public boolean isNameUsed(String username){
        for (ClientHandler clts:clients) {
            if(clts.getName().equalsIgnoreCase(username)){
                return true;
            }
        }
        return false;
    }

    public synchronized void unsubscribe(ClientHandler c){//при отключении клиента (завершение его процесса Main)
        // сервер удаляет его из своего списка участников и оповещает остальных клиентов об уходе пользователя
        String name=c.getName();
        clients.remove(c);
        broadcastMessage("Из чата вышел пользователь "+name);
        broadcastClientList();
    }

    public synchronized void broadcastMessage(String message){//рассылка сообщений всем подключенным клиентам

        for (ClientHandler clts:clients) {
            clts.sendMessage(message);
        }
    }

    public synchronized void broadcastClientList(){//рассылка сообщений всем подключенным клиентам
        StringBuilder builder=new StringBuilder(clients.size()*10);
        builder.append("/clients_list ");
        for (ClientHandler clts:clients) {
            builder.append(clts.getName()).append(" ");
        }
        String clientsListStr=builder.toString();
        broadcastMessage(clientsListStr);
    }

    public synchronized void sendPersonalMessage(ClientHandler sender, String receiverUsername, String message){
        if(sender.getName().equalsIgnoreCase(receiverUsername)){
            sender.sendMessage("Нельзя отправлять личные сообщения самому себе");
            return;
        }
        for(ClientHandler c: clients){
            if(c.getName().equalsIgnoreCase(receiverUsername)){
                c.sendMessage("from "+sender.getName()+": "+message);
                sender.sendMessage("to user "+receiverUsername+": "+message);
                return;
            }
        }
        sender.sendMessage("User "+receiverUsername+" не в сети.");
    }
}
