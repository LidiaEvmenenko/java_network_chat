package ru.geekbrains.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Server {
    private AuthentificationProvider authentificationProvider;
    private List<ClientHandler> clients;
    private ExecutorService executorService;

    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    public AuthentificationProvider getAuthentificationProvider() {
        return authentificationProvider;
    }

    public Server(){ //запуск сервера, создание списка для добавления туда клиентов, запуск процесса добавления клиентов
        try {
            //this.authentificationProvider= new InMemoryAuthentificationProvider();
            executorService = Executors.newCachedThreadPool();
            this.clients= new ArrayList<>();
            ServerSocket serverSocket = new ServerSocket(8189);
            LOGGER.info("Сервер запущен. Ожидаем подключение клиентов..");
            AtomicInteger clientsCount= new AtomicInteger();
            executorService.execute(() -> {
                authentificationProvider= new DataBaseAuthentificationProvider();
                authentificationProvider.start();
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
                    } catch (IOException e) {
                        LOGGER.error("Не удалось запустить socket.",e);
                    }
                       clientsCount.getAndIncrement();
                    LOGGER.info("Клиент №" + clientsCount + " подключился");
                    ClientHandler c = new ClientHandler(this, socket);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Не удалось запустить сервер.",e);
        } finally {
            if (authentificationProvider != null) {
                authentificationProvider.stop();
            }
            executorServiceShutdown();
        }
    }

    public synchronized void subscribe(ClientHandler c) { //проверка имени клиента,
        // в случае уникальности клиент добавляется в список активных клиентов сервера - участники чата
        broadcastMessage("К чату подключился пользователь " + c.getName());
        clients.add(c);
        broadcastClientList();
    }

    public boolean isNameUsed(String username) {
        for (ClientHandler clts : clients) {
            if (clts.getName().equalsIgnoreCase(username)) {
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
        LOGGER.info(message);
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
            sender.sendMessage("Нельзя отправлять личные сообщения самому себе.");
            LOGGER.warn("To "+receiverUsername+": Нельзя отправлять личные сообщения самому себе.");
            return;
        }
        for(ClientHandler c: clients){
            if(c.getName().equalsIgnoreCase(receiverUsername)){
                c.sendMessage("from "+sender.getName()+": "+message);
                sender.sendMessage("to user "+receiverUsername+": "+message);
                LOGGER.info("From "+sender.getName()+" to user "+ receiverUsername+ ": "+message);
                return;
            }
        }
        sender.sendMessage("User "+receiverUsername+" не в сети.");
        LOGGER.warn("User "+receiverUsername+" не в сети.");
    }

    public void executorServiceShutdown() {
        executorService.shutdown();
        try { // ждем 1 секунду, чтобы потоки завершились
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Ошибка закрытия executorService.",e);
            executorService.shutdownNow();
        }
    }

}
