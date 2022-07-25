package hr.algebra.controllers;

import hr.algebra.model.User;
import hr.algebra.repository.ISqlRepo;
import hr.algebra.repository.RepoFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private ISqlRepo sqlRepo;



    @FXML
    private AnchorPane fxLoginAnchor;

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignIn;

    @FXML
    private Button btnExit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            sqlRepo = RepoFactory.getRepository();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void validateLogin() {
        Thread taskThread = new Thread(() -> {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        try {
         User user = sqlRepo.validateUser(username,password);
           if (user.getRole() == 1){
            setStage("AdminHome",user);
           }
            else{
                setStage("UserHome",user);

            }
        } catch (Exception e) {
           setLblError(Color.RED,"Invalid Login Info");
        }
    });
        taskThread.setDaemon(true);
        taskThread.run();

    }

    @FXML
    private void exitLogin(){
        Stage stage =(Stage) fxLoginAnchor.getScene().getWindow();
        stage.close();
    }



    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
    }


    private void setStage(String stageName,User user) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("hr/algebra/view/" + stageName + ".fxml"));
            Platform.runLater(() -> {
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MainDashboardController mainDashboardController = loader.getController();
                mainDashboardController.setUserFromLogin(user);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                Stage out = (Stage) fxLoginAnchor.getScene().getWindow();
                out.close();
            });
    }
}
