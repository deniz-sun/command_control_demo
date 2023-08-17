package quokka;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quokka.controller.MapController;
import quokka.controller.SongTableController;

import java.io.IOException;
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        try {

       //     SongTableController controller = new SongTableController();
    //        MapController controller = new MapController();
        //  SongController controller = new SongController();
            Parent root = FXMLLoader. load(getClass().getResource("/quokka/controller/LoginPage.fxml"));

           Scene scene = new Scene(root);
     //       stage.getIcons().add(new Image("src/resources/musiclogo.png"));
           stage.setTitle("Songs Application");
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
