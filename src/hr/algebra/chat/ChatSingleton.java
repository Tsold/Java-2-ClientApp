package hr.algebra.chat;

import hr.algebra.controllers.ChatController;
import hr.algebra.model.User;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ChatSingleton {


    private ChatSingleton() {}

    private static Parent instance;

    public static Parent getChatInstance(User mainUser, Stage stage) throws IOException {
        if (instance == null) {
            instance = createInstance(mainUser,stage);
        }
        return instance;
    }

    private static Parent createInstance(User mainUser, Stage thisStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ChatSingleton.class.getClassLoader().getResource("hr/algebra/view/UserChat.fxml"));
        Parent root =  loader.load();
        ChatController chatController = loader.getController();
        chatController.setUserFromLogin(mainUser);
        thisStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                chatController.closeThis();
                Platform.exit();
                System.exit(0);
            }
        });
        return root;
    }


}
