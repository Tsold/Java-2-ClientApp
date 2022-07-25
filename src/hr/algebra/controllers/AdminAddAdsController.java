package hr.algebra.controllers;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import hr.algebra.documentmaker.DocumentGenerator;
import hr.algebra.model.Advertisement;
import hr.algebra.model.Vehicle;
import hr.algebra.repository.AdvertisementRepo;
import hr.algebra.repository.ISqlRepo;
import hr.algebra.repository.RepoFactory;
import hr.algebra.repository.VehiclesRepo;
import hr.algebra.utils.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminAddAdsController implements Initializable {

    ISqlRepo repo;

    @FXML
    private TableView<Advertisement> tvAds;

    @FXML
    private TextField tfTitle,tfPrice;

    @FXML
    private TableColumn<Advertisement, String> tcTitle, tcPrice,tcDateAndTime;

    @FXML
    private ComboBox<Vehicle> cbVehicles;


    @FXML
    private Button btnAddAd,btnAddSubject;




    @Override
    public void initialize(URL location, ResourceBundle resources)  {
        Thread taskThread = new Thread(() -> { try {
            repo = RepoFactory.getRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }

            try {
                initObservables();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
              initTableCells();
            });
        });
        taskThread.setDaemon(true);
        taskThread.run();


    }

    private void initObservables() throws Exception {

            List<Advertisement> ads = null;
            try {
                ads = repo.getAllAds();
            } catch (Exception e) {
                e.printStackTrace();
            }
            AdvertisementRepo.getInstance().setAds(ads,0);
            List<Vehicle> vehs = null;
            try {
                vehs = repo.getVehicles();
            } catch (Exception e) {
                e.printStackTrace();
            }
            VehiclesRepo.getInstance().setVehicles(vehs);


    }

    private void initTableCells() {
        cbVehicles.setItems(VehiclesRepo.getInstance().getFreeVehicles());
        tvAds.setItems(AdvertisementRepo.getInstance().getAds());
        tcTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        tcTitle.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDateAndTime.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        String.format(c.getValue().getDateAndTime().toString(),"yyyy-MM-dd HH:mm:ss")));

    }

    @FXML
    private void addAd() throws Exception {

        Thread taskThread = new Thread(() -> {
            int vehId =cbVehicles.getSelectionModel().getSelectedItem().getIDVehicle();
            Vehicle removedVeh =cbVehicles.getSelectionModel().getSelectedItem();

            int lastId = AdvertisementRepo.getInstance().getAds().size() +1;
            Advertisement ad = new Advertisement(lastId, vehId,tfTitle.getText().trim(),
                    Integer.parseInt(tfPrice.getText().trim()), java.sql.Date.valueOf(LocalDate.now()),false,1);

            try {
                repo.addAdvertisement(ad);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                repo.updateVehicleNotAvailable(vehId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AdvertisementRepo.getInstance().addAd(ad);
            VehiclesRepo.getInstance().getVehicles().remove(removedVeh);
            VehiclesRepo.getInstance().getFreeVehicles().remove(removedVeh);
            clearForm();
            btnAddAd.setDisable(true);

        });
        taskThread.setDaemon(true);
        taskThread.run();


    }

    @FXML
    private void addVehicle() throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("hr/algebra/view/AddVehicle.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Vehicles");
        stage.setScene(new Scene(root));

        stage.show();


    }

    @FXML
    private void deleteAd(MouseEvent event) throws Exception {
        if(event.getClickCount() != 2 || event.getTarget().getClass().equals(TableColumnHeader.class)) {
            return;
        }

        Thread taskThread = new Thread(() -> {
        int selectedAd = tvAds.getSelectionModel().getSelectedIndex();
        int selectedIndex = tvAds.getSelectionModel().getSelectedItem().getIdAdvertisement();
        int selectedVehicle = tvAds.getSelectionModel().getSelectedItem().getVehicleID();
        if (selectedIndex != -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Delete ad?");

            alert.setContentText("Are you sure?");
            if (alert.showAndWait().get() == ButtonType.OK){
                AdvertisementRepo.getInstance().getAds().remove(selectedAd);
                Vehicle vehicle = VehiclesRepo.getInstance().getSingleVehicle(selectedVehicle);
                VehiclesRepo.getInstance().getFreeVehicles().add(vehicle);
                try {
                    repo.updateVehicleAvailable(selectedVehicle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    repo.deleteAd(selectedIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        });
        taskThread.setDaemon(true);
        taskThread.run();


    }




    private void clearForm() {
        tfPrice.clear();
        cbVehicles.getSelectionModel().clearSelection();
        tfTitle.clear();
    }



    @FXML
    private void createDocumentation() {DocumentGenerator.docMaker();}


    //Serijalizacija i deserijalizacija

    @FXML
    private void deserialize() {
        File file = FileUtils.uploadFileDialog(tvAds.getScene().getWindow(), "ser");
        if (file != null) {
            try {
                SerializationUtils.read(file.getAbsolutePath());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(AdminAddAdsController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void serialize() {
        try {
            File file = FileUtils.saveFileDialog(tvAds.getScene().getWindow(), "ser");
            if (file != null) {
                SerializationUtils.write(AdvertisementRepo.getInstance(),VehiclesRepo.getInstance(), file.getAbsolutePath());
            }
        } catch (IOException ex) {
            Logger.getLogger(AdminAddAdsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


//Xml

    @FXML
    private void loadDOM() {
        AdvertisementRepo.getInstance().swapAds(DOMUtils.loadAds(),DOMUtils.loadVehicles());
    }

    @FXML
    private void saveDOM() throws ParserConfigurationException {
        DOMUtils.saveAdvertisements(AdvertisementRepo.getInstance().getAds());
        DOMUtils.saveVehicles(VehiclesRepo.getInstance().getVehicles());
        MessageUtils.showInfoMessage("DOM", "XML documents saved", "Saved with no failures");
    }

    @FXML
    private void loadSAX() {
        AdvertisementRepo.getInstance().swapAds(SAXUtils.loadAds(),SAXUtils.loadVehicles());
    }




    @FXML
    private void handleAddCarButton() {
        btnAddAd.setDisable(
                cbVehicles.getSelectionModel().getSelectedItem() == null
                        || tfPrice.getText().trim().isEmpty()
                        || tfTitle.getText().trim().isEmpty()
        );
    }



}
