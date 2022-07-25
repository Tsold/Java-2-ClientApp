package hr.algebra.controllers;

import hr.algebra.model.Vehicle;
import hr.algebra.repository.ISqlRepo;
import hr.algebra.repository.RepoFactory;
import hr.algebra.repository.VehiclesRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddVehicleController implements Initializable {

    ISqlRepo repo;

    @FXML
    private TextField tfMaker,tfModel,tfKm;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private Button btnAddCar;

    @FXML
    private DatePicker dpYear;




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            repo = RepoFactory.getRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            initTableCells();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initTableCells() {
        cbType.setItems(getType());
    }

    private ObservableList<String> getType(){
        List<String> type = new ArrayList<>();
        type.add("Sedan");
        type.add("Hatchback");
        type.add("N1");
        type.add("Wagon");
        type.add("Coupe");
        ObservableList<String> types;
        return types =  FXCollections.observableArrayList(type);
    }



    @FXML
    private void addCar()  {
        Thread taskThread = new Thread(() -> {
            int lastId = VehiclesRepo.getInstance().getVehicles().size() +1;
            Vehicle vehicle = new Vehicle(lastId,cbType.getSelectionModel().getSelectedIndex()+1,tfMaker.getText().trim(),tfModel.getText().trim(),
                    java.sql.Date.valueOf( dpYear.getValue()).toString(),  Integer.parseInt(tfKm.getText().trim()),1);
            try {
                repo.addVehicle(vehicle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            VehiclesRepo.getInstance().addVehicle(vehicle);
            clearForm();


        });
        taskThread.setDaemon(true);
        taskThread.run();
    }



    private void clearForm() {
        tfKm.clear();
        tfMaker.clear();
        tfModel.clear();
        dpYear.getEditor().clear();
    }





}
