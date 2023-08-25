package quokka.controller;


import quokka.models.Account;
import quokka.JavaPostgreSql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    public TextField loginEmail, loginPassword;
    private Stage stage;
    private Scene scene;


    @FXML
    private void handleLogin(ActionEvent event) {
        String email = loginEmail.getText();
        char[] password = loginPassword.getText().toCharArray();

        Account account = JavaPostgreSql.authenticateUserAndGetAccount(email, password);

        if (account != null) {
            // Authentication successful, proceed to load songs data and switch to SongPage
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Map.fxml"));
                Parent root = loader.load();

                /*
                SongController songController = loader.getController();

                songController.collectUser(account);
                songController.showWelcome();
                */

                MapController mapController = loader.getController();
                mapController.collectUser(account);

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                AccountController.showAlert("Error!", null, "Error loading the home page.");
            }

        }
        else {
            // Display an error message for failed login
            AccountController.showAlert("Login Error", "Invalid email or password.", "Error");
        }
    }




    public void goToRegister(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("RegisterPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AccountController.showAlert("Error!", null, "Error loading the login page.");
        }
    }


}