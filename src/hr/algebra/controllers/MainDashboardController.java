package hr.algebra.controllers;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import hr.algebra.model.Advertisement;
import hr.algebra.model.User;
import hr.algebra.model.Vehicle;
import hr.algebra.chat.ChatSingleton;
import hr.algebra.threads.ReceiveThread;
import hr.algebra.threads.SendThread;
import hr.algebra.utils.MessageUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainDashboardController implements Initializable {

    private User mainUser;

    private ReceiveThread receiveThread;

    private SendThread sendThread;

    @FXML
    private TableView<Vehicle> tvVehicles;

    @FXML
    private TableColumn<Advertisement, String> tcMakerModel, tcYear,tcStatus;

    @FXML
    private BorderPane fxBorderPane;

    @FXML
    private Button btnOverview;

    @FXML
    private Button btnChat;

    @FXML
    private Button btnTakenMenu;

    @FXML
    private Button btnExitApp;

    @FXML
    private Label LbName;


    private ObservableList<Vehicle> vehicles;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void initTables() {

            tvVehicles.setItems(vehicles);
            tcMakerModel.setCellValueFactory(new PropertyValueFactory<>("Maker"));

            if (mainUser.getIdUser() == 1) {
                tcYear.setCellValueFactory(new PropertyValueFactory<>("ProductionYear"));
            }
            else
                tcStatus.setCellValueFactory(new PropertyValueFactory<>("Model"));


    }

    private void initObservables() {
        List<Vehicle> vehs = new ArrayList<>();
        vehicles = FXCollections.observableArrayList(vehs);
    }


    public void setUserFromLogin(User user){
        mainUser = user;
        LbName.setText("Welcome "+ mainUser.toFullName());

        initThread();
        Thread taskThread = new Thread(() -> {
            initObservables();

            Platform.runLater(() -> {
                initTables();
                if (mainUser.getRole() == 1){
                    try {
                        homePanel();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(mainUser.getRole() != 1){
                    try {
                        userAds();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        });
        taskThread.setDaemon(true);
        taskThread.run();


    }




    private void initThread() {


        if (mainUser.getRole() == 1) {
            sendThread = new SendThread();
            sendThread.setDaemon(true);
            sendThread.start();
            receiveThread = new ReceiveThread(this);
            receiveThread.setDaemon(true);
            receiveThread.start();
        }
        if (mainUser.getRole() != 1) {
            receiveThread = new ReceiveThread(this);
            receiveThread.setDaemon(true);
            receiveThread.start();
        }

    }

    public void addToList(Vehicle vehicle)
    {
        if (vehicle.getAvailable() == 1 && mainUser.getIdUser() == 1)
        {
            vehicles.add(vehicle);
        }
        if(vehicle.getIDVehicle() == mainUser.getIdUser() && vehicle.getAvailable() == 0)
        {
            vehicles.add(vehicle);
        }

    }

    @FXML
    private void homePanel() throws IOException {

        loadUi("AdminAddAds",mainUser);


    }


    @FXML
    private void userAds() throws IOException {


        loadUi("UserAds",mainUser);

    }




    @FXML
    private void appExit(){
        Stage stage =(Stage) fxBorderPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void adsTaken() throws IOException {
        loadUi("UserTakenAds",mainUser);
    }


    @FXML
    public void acceptVehicle(MouseEvent event) {
        if(event.getClickCount() != 2 || event.getTarget().getClass().equals(TableColumnHeader.class)) {
            return;
        }

        Thread taskThread = new Thread(() -> {
        int selectedVeh = tvVehicles.getSelectionModel().getSelectedIndex();
        int selectedIndex = tvVehicles.getSelectionModel().getSelectedItem().getIDVehicle();
        Vehicle selectedVehicle = tvVehicles.getSelectionModel().getSelectedItem();
        selectedVehicle.setAvailable(0);
        if (selectedIndex != -1) {
            ButtonType accept = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
            ButtonType deny = new ButtonType("Deny", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Accept Request?",accept,deny);
            alert.setTitle("Confirmation");
            alert.setContentText(selectedVehicle.toSendInfo());

            ButtonType chosenOne = alert.showAndWait().get();

            if (chosenOne == accept){
                selectedVehicle.setModel("Accepted");
                sendThread.trigger(selectedVehicle);
                vehicles.remove(selectedVeh);
                           }
            if (chosenOne == deny){
                selectedVehicle.setModel("Denied");
                sendThread.trigger(selectedVehicle);
                vehicles.remove(selectedVeh);
            }

        }
        });
        taskThread.setDaemon(true);
        taskThread.run();

    }



    @FXML
    public void loadChat() {
        try {
            loadUi();
        }catch (Exception ex)
        {
            MessageUtils.showInfoMessage("Connection Error", "Server offline", "Try again later");
            Stage thisStage = (Stage) btnChat.getScene().getWindow();
            thisStage.setOnCloseRequest(event -> System.exit(0));
        }
    }


    private void loadUi() throws IOException {
        Stage thisStage = (Stage) btnChat.getScene().getWindow();
        fxBorderPane.setCenter(ChatSingleton.getChatInstance(mainUser,thisStage));
    }


    private void loadUi(String ui,User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("hr/algebra/view/"+ui+".fxml"));
        Parent root =  loader.load();
        if (ui == "UserAds"){
            UserAdsController studentAdsController = loader.getController();
            studentAdsController.setUserFromLogin(user);
        }
        else if (ui == "UserTakenAds"){
            UserTakenAdsController studentTakenAdsController = loader.getController();
            try {
                studentTakenAdsController.setUserFromLogin(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fxBorderPane.setCenter(root);

        }
}
