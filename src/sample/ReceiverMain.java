package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReceiverMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("receiver.fxml"));
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(root, 350, 200));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
