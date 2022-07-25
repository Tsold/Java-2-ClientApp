package hr.algebra.controllers;

import hr.algebra.chat.ChatClient;
import hr.algebra.model.User;
import hr.algebra.utils.MessageUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    private User mainUser;

    @FXML
    private ListView<String> userList;

    @FXML
    private TextArea window;

    @FXML
    private TextField input;

    @FXML
    private Button btnRequest;

    private ChatClient client;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.input = this.getTextField();
    }

    private void serverLogin(String s) {
        this.initClient();
        try {
            this.client.serverLogin(s);
        } catch (RemoteException e) {
            System.out.println(e);
        }

    }

    @FXML
    private void requestVehicle() throws IOException {
        Thread t = new Thread(() -> {
            try {
                requestLogin();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }


    private void requestLogin() throws IOException {

        if (client.requestLogin(mainUser.getIdUser())) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("hr/algebra/view/SendRequest.fxml"));
            Parent root = loader.load();
            SendRequestController SendRequestController = loader.getController();
            SendRequestController.setUserFromLogin(mainUser);

            Platform.runLater(() -> {
                Stage stage = new Stage();
                        stage.setTitle("Request");
                        stage.setScene(new Scene(root));
                        stage.show();
                        btnRequest.setDisable(true);
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        try {
                            client.logoutRequest();
                            btnRequest.setDisable(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
                    }
            );


        } else {
            Platform.runLater(() ->   MessageUtils.showInfoMessage("Taken", "Someone is currently requesting a Vehicle", "Try again later"));
        }

    }




    private void initClient() {
        if (this.client == null)
            try {
                this.client = new ChatClient(this);
            } catch (RemoteException e) {
                System.out.println(e);
                this.closeThis();
            } catch (MalformedURLException | NotBoundException e) {
                System.out.println(e);
                this.closeThis();
            }
    }

    private TextField getTextField() {
        input.setOnAction(e -> {
            client.sendMessage(input.getText());
            input.setText("");
        });
        return input;
    }


    public void closeThis() {
        new Thread(() -> client.logout()).start();
    }


    public void setMessage(String message) {
        Platform.runLater(() -> this.window.appendText("\n" + message));
    }

    public void newUserList(String[] userList2) {
        Platform.runLater(() -> {
            this.userList.setItems(null);
            ObservableList<String> list = FXCollections.observableArrayList(userList2);
            FXCollections.sort(list);
            this.userList.setItems(list);
        });
    }

    public void setUserFromLogin(User user) {
        mainUser = user;
        this.serverLogin(mainUser.toFullName());
        if (mainUser.getRole() == 1) {
            btnRequest.setVisible(false);
        }
    }
}
