package quokka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        try {
            Parent root = FXMLLoader. load(getClass().getResource("/quokka/controller/RegisterPage.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Management App");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}