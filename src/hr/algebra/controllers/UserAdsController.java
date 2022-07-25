package hr.algebra.controllers;

import com.sun.javafx.scene.control.skin.TableColumnHeader;
import hr.algebra.documentmaker.DocumentGenerator;
import hr.algebra.model.Advertisement;
import hr.algebra.model.User;
import hr.algebra.model.Vehicle;
import hr.algebra.repository.AdvertisementRepo;
import hr.algebra.repository.ISqlRepo;
import hr.algebra.repository.RepoFactory;
import hr.algebra.utils.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAdsController implements Initializable {



    ISqlRepo repo;

    private  User mainUser;

    @FXML
    private TableView<Advertisement> tvAds;

    @FXML
    private TableColumn<Advertisement, String> tcTitle, tcPrice,tcDateAndTime;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        List<Advertisement> ads = repo.getAllAds();
        AdvertisementRepo.getInstance().setAds(ads,0);
    }

    private void initTableCells() {

        tvAds.setItems(AdvertisementRepo.getInstance().getFreeAds());
        tcTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        tcTitle.setCellFactory(TextFieldTableCell.forTableColumn());
        tcDateAndTime.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        String.format(c.getValue().getDateAndTime().toString(),"yyyy-MM-dd HH:mm:ss")));
    }

    @FXML
    private void reserveAd(MouseEvent event) throws Exception {

        if(event.getClickCount() != 2 || event.getTarget().getClass().equals(TableColumnHeader.class)) {
            return;
        }
        int selectedIndex = tvAds.getSelectionModel().getSelectedIndex();
        Advertisement remAd = tvAds.getSelectionModel().getSelectedItem();
        int Id = tvAds.getSelectionModel().getSelectedItem().getIdAdvertisement();
        int vehicleId = tvAds.getSelectionModel().getSelectedItem().getVehicleID();
        Vehicle vehicle = repo.getSingleVehicle(vehicleId);
        if (selectedIndex != -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Preorder Car?");

            alert.setContentText(vehicle.toFullInfo());
            if (alert.showAndWait().get() == ButtonType.OK){
                AdvertisementRepo.getInstance().getFreeAds().remove(selectedIndex);
                AdvertisementRepo.getInstance().getAds().remove(remAd);
                repo.updateAd(Id,mainUser.getIdUser());
            }
        }

    }


    @FXML
    private void createDocumentation() {DocumentGenerator.docMaker();}



    @FXML
    private void deserialize() {
        File file = FileUtils.uploadFileDialog(tvAds.getScene().getWindow(), "ser");
        if (file != null) {
            try {
                SerializationUtils.read(file.getAbsolutePath());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(UserAdsController.class.getName()).log(Level.SEVERE, null, ex);
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
                SerializationUtils.write(AdvertisementRepo.getInstance(), file.getAbsolutePath());
            }
        } catch (IOException ex) {
            Logger.getLogger(UserAdsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    private void loadDOM() {
        AdvertisementRepo.getInstance().swapAds(DOMUtils.loadAds());
    }

    @FXML
    private void saveDOM() throws ParserConfigurationException {
        DOMUtils.saveAdvertisements(AdvertisementRepo.getInstance().getAds());
        MessageUtils.showInfoMessage("DOM", "XML documents saved", "Saved with no failures");
    }

    @FXML
    private void loadSAX() {
        AdvertisementRepo.getInstance().swapAds(SAXUtils.loadAds());
    }



    public void setUserFromLogin(User user){
        mainUser = user;
    }


}
