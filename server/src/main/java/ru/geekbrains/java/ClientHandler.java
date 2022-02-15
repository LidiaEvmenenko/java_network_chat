package ru.geekbrains.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String name;
    private DataInputStream in;
    private DataOutputStream out;

    public String getName(){//узнаем имя клиента
        return this.name;
    }

    public ClientHandler(Server server,Socket socket){
        //создание нового клиентского подключения к серверу с предварительной авторизацией,
        // дальнейшим участием в чате (в случае успешной авторизации) и отключением после остановки процесса Main клиента
        try {
            this.server=server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            Thread clientThread = new Thread(() -> {
                logic();
            });
            clientThread.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void logic(){
        try {
            while (consumeAuthorizeMessage(in.readUTF()));
            while (consumeRegularMessage(in.readUTF()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.unsubscribe(this);
            closeConnection();
        }
    }

    private boolean consumeAuthorizeMessage(String message){
        if(message.startsWith("/auth ")){
            String[] tokens =message.split("\\s+");
            if(tokens.length!=3){
                sendMessage("SERVER: Неверно сформирован запрос на авторизацию.");
                return true;
            }
            String login=tokens[1];
            String password=tokens[2];

            String selectedUserName=server.getAuthentificationProvider().getUsernameByLoginAndPassword(login, password);
            if(selectedUserName == null){
                sendMessage("SERVER: Неверно указан логин/пароль.");
                return true;
            }
            if(server.isNameUsed(selectedUserName)){
                sendMessage("SERVER: Пользователь с таким именем уже подключился.\nSERVER: Выберите другое имя.");
                return true;
            }
            name=selectedUserName;
            sendMessage("/authok "+name);
            server.subscribe(this);
            return false;

        } else {
            sendMessage("Server: Вам необходимо авторизоваться.");
            return true;
        }
    }

    private boolean consumeRegularMessage(String inputMessage){
        if(inputMessage.startsWith("/")){
            if(inputMessage.equals("/exit")){
                sendMessage("/exit");
                return false;
            }
            if(inputMessage.startsWith("/w ")){
                String[] tokens=inputMessage.split("\\s+",3);
                server.sendPersonalMessage(this,tokens[1],tokens[2]);
            }
            return true;
        }
        server.broadcastMessage(name+": "+inputMessage);
        return true;
    }

    public void sendMessage(String message)  {//отправка сообщения клиенту, текст отобразится в поле textAreaHistory
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection(){
        try{
            if(in!=null){
                in.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        try{
            if(out!=null){
                out.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        try{
            if(socket!=null){
                socket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
