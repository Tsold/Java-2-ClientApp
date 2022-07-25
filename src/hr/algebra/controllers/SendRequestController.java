package hr.algebra.controllers;

import hr.algebra.model.User;
import hr.algebra.model.Vehicle;
import hr.algebra.threads.SendThread;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.ResourceBundle;

public class SendRequestController implements Initializable {

    private SendThread serverThread;

    private  User mainUser;

    @FXML
    private TextField tfMaker,tfModel,tfKm,tfProductionYear,tfUserRequest;

    @FXML
    Button btnSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    initThread();
    }


    @FXML
    private void Send() {
        serverThread.trigger(new Vehicle(mainUser.getIdUser(),1,
                tfMaker.getText().trim()+" "+ tfModel.getText().trim(),
                tfModel.getText().trim(),
                tfProductionYear.getText().trim(),
                Integer.parseInt(tfKm.getText().trim()),1));
        clearForm();
    }

    private void clearForm(){

        tfMaker.clear();
        tfModel.clear();
        tfKm.clear();
        tfProductionYear.clear();

    }



    private void initThread() {
        serverThread = new SendThread();
        serverThread.setDaemon(true);
        serverThread.start();
    }


    @FXML
    private void handleSendButton() {
        btnSend.setDisable(
                tfMaker.getText().trim().isEmpty() ||
                        tfModel.getText().trim().isEmpty() ||
                        tfKm.getText().trim().isEmpty() ||
                        tfProductionYear.getText().trim().isEmpty()
        );
    }


    public void setUserFromLogin(User user){
        mainUser = user;
    }



}
