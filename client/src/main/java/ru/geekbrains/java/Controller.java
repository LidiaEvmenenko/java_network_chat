package ru.geekbrains.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller{

    @FXML
    TextField textFieldMessage, textFieldLogIn;
    @FXML
    TextArea textAreaHistory;
    @FXML
    HBox authPanel, msgPanel;
    @FXML
    ListView<String> clientsListView;
    @FXML
    Label labelUserName;
    @FXML
    PasswordField textFieldPassword;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String tryUserName;

    public void setAutorized(boolean autorized){
        msgPanel.setVisible(autorized);
        msgPanel.setManaged(autorized);
        authPanel.setVisible(!autorized);
        authPanel.setManaged(!autorized);
        clientsListView.setVisible(autorized);
        clientsListView.setManaged(autorized);
    }

    public void sendMessage() { //отправка сообщения клиентом всем участникам чата
        try {
            if(!textFieldMessage.getText().isEmpty()) {
                out.writeUTF(textFieldMessage.getText());
                textFieldMessage.clear();
                textFieldMessage.requestFocus();
            }
        }catch(IOException e){
            showError("Невозможно отправить сообщение на сервер.");
        }
    }

    public void sendCloseRequest() {
        try {
            if(out!=null) {
                out.writeUTF("/exit");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void tryToAuth() {//отправка серверу сообщения с просьбой об авторизации нового клиента
        // под определенным именем
        connect();
        try {
            tryUserName=textFieldLogIn.getText();
            out.writeUTF("/auth "+ tryUserName+" "+textFieldPassword.getText());
            textFieldLogIn.clear();
            textFieldPassword.clear();
        } catch (IOException e) {
            showError("Невозможно направить запрос авторизации на сервер.");
        }
    }

    public  void connect(){// подключение к серверу с предварительной авторизацией и дальнейшим добавлением сообщений
        // в общий чат от имени клиента
        if(socket!=null && !socket.isClosed()) {
            return;
        }
            try {
                socket = new Socket("localhost", 8189);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                new Thread(() -> mainClientLogic()).start();
            } catch (IOException e) {
                showError("Не удается соединиться с сервером.");
                System.exit(0);
            }

    }

    private void mainClientLogic(){
        try {
            while (true) {
                String inputMessage = in.readUTF();
                if(inputMessage.equals("/exit")){
                    closeConnection();
                }
                if(inputMessage.startsWith("/authok")){
                    setAutorized(true);
                    tryUserName=inputMessage.split("\\s+")[1];
                    Platform.runLater(()->
                    {
                        labelUserName.setText(tryUserName);
                    });
                    break;
                }
                textAreaHistory.appendText(inputMessage + "\n");
            }
            while (true) {
                String inputMessage = in.readUTF();
                if(inputMessage.startsWith("/")){
                    if(inputMessage.equals("/exit")){
                        break;
                    }
                    if(inputMessage.startsWith("/clients_list ")) {
                        Platform.runLater(() ->
                        {
                            String[] tokens = inputMessage.split("\\s+");
                            clientsListView.getItems().clear();
                            for (int i = 1; i < tokens.length; i++) {
                                clientsListView.getItems().add(tokens[i]);
                            }
                        });
                    }
                    if(inputMessage.startsWith("/newnameok ")){
                        Platform.runLater(()->
                        {
                            String[] tokens = inputMessage.split("\\s+");
                            clientsListView.getItems().clear();
                            labelUserName.setText(tokens[1]);
                            for (int i = 1; i < tokens.length; i++) {
                                clientsListView.getItems().add(tokens[i]);
                            }
                        });
                    }
                    continue;
                }
                textAreaHistory.appendText(inputMessage + "\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void closeConnection(){
        setAutorized(false);
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

    public void showError(String message){//метод вывода понятных ошибок пользователю
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    public void clientsListDoubleClick(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==2){
            String selectedUser=clientsListView.getSelectionModel().getSelectedItem();
            textFieldMessage.setText("/w "+selectedUser+ " ");
            textFieldMessage.requestFocus();
            textFieldMessage.selectEnd();
        }
    }
}
