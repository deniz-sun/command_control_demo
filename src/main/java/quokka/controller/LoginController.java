package quokka.controller;


import quokka.models.Account;
import quokka.JavaPostgreSql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import quokka.models.Song;

import java.io.IOException;
import java.util.Arrays;

public class LoginController {

    @FXML
    public TextField loginEmail, loginPassword;
    private Stage stage;
    private Scene scene;
    private Parent root;


    public Account getCurrentAccount() {
        return currentAccount;
    }

    private Account currentAccount;

    /**
     *
     */


    @FXML
    private void handleLogin(ActionEvent event) {
        String email = loginEmail.getText();
        char[] password = loginPassword.getText().toCharArray();

        Account account = JavaPostgreSql.authenticateUserAndGetAccount(email, password);

        if (account != null) {
            // Authentication successful, proceed to load songs data and switch to SongPage
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("BrowseSongsPage.fxml"));
                Parent root = loader.load();
                SongController songController = loader.getController();
                songController.collectUser(account);
                songController.showWelcome();
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                AccountController.showAlert("Error!", null, "Error loading the home page.");
            }
            System.out.println(account);


            //    SongController.collectUser(account);    // this one has error
            System.out.println("after collecting account"); //doesn't reach here
            //songController.showWelcome();

        } else {
            // Display an error message for failed login
            AccountController.showAlert("Login Error", "Invalid email or password.", "Error");
        }
    }


/*
    public void handleLogin(ActionEvent event) throws IOException {
        String email = loginEmail.getText();
        char[] password = loginPassword.getText().toCharArray();

        Account account = JavaPostgreSql.getAccountByEmail(email);
        String emailInDatabase = account.getEmail();
        char[] passwordInDatabase = account.getPassword();


        if (email.isEmpty()){
            AccountController.showAlert("Form Error!", null, "Please enter your email");
            return;
        }
        if (password.length == 0){
            AccountController.showAlert("Form Error!", null, "Please enter your password");
            return;
        }


        if (emailInDatabase.equals(email) && Arrays.equals(passwordInDatabase, password)) {
            currentAccount = account;
            AccountController.showSuccess("User: " + email,"Login Successful", "Success!");


            goToHomePage(event); //error


            SongController songController = new SongController();
            songController.collectUser(currentAccount);
            songController.showWelcome();




}        else {
            AccountController.showAlert("Failed login", "Error","Incorrect email or password");
        }
    }
    */

    public void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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