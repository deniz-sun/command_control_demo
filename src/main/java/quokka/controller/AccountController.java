package quokka.controller;

import quokka.JavaPostgreSql;
import quokka.models.Account;
//import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import quokka.models.Song;

import java.io.IOException;
import java.util.List;


public class AccountController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public PasswordField password;
    @FXML
    private TableView<Account> savedAccounts, fetchByEmailTable;

    @FXML
    private TableColumn<Account, Integer> fetchEmailIdColumn;

    @FXML
    private TableColumn<Account, String> fetchEmailFirstNameColumn, fetchEmailLastNameColumn;

    @FXML
    private TableColumn<Account, Integer> savedId;

    @FXML
    private TableColumn<Account, String> savedFirstName, savedLastName, savedEmail;



    @FXML
    public Label first_nameLabel1, last_nameLabel1;
    @FXML
    public TextField idEntry, emailEntry;
    @FXML
    public Label addedUserLabel;

    @FXML
    private TextField first_name, last_name, email;

    @FXML
    private ListView<Song> addedSongsListView;

    public void register(ActionEvent event) {
        System.out.println(first_name.getText());
        System.out.println(last_name.getText());
        int savedAccountId = JavaPostgreSql.saveAccount(first_name.getText(), last_name.getText(), email.getText(), password.getText().toCharArray());
        showSuccess("Account created successfully",null, "Success!");

        //addedUserLabel.setText("User has been added with the id: " + savedAccountId );
/*
        savedId.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getId()));
        savedFirstName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFirstName()));
        savedLastName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastName()));
        savedEmail.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEmail()));

        savedAccounts.setItems(JavaPostgreSql.getAllAccounts());
*/
    }

    public void searchById(ActionEvent event) {
        Account account = JavaPostgreSql.getAccountById(Integer.parseInt(idEntry.getText()));
        first_nameLabel1.setText(account.getFirstName());
        last_nameLabel1.setText(account.getLastName());

    }

    public void searchByEmail(ActionEvent event) {
        List<Account> accounts = JavaPostgreSql.getAccountsByEmail(emailEntry.getText());
        ObservableList<Account> accountList = FXCollections.observableArrayList(accounts);

        if (accounts.isEmpty()) {
            showAlert("Account Search", null, "No accounts found with the provided email.");

        } else {
            StringBuilder accountInfo = new StringBuilder();
            for (Account account : accounts) {
                accountInfo.append("ID: ").append(account.getId())
                        .append(", First Name: ").append(account.getFirstName())
                        .append(", Last Name: ").append(account.getLastName())
                        .append("\n");
            }
            showSuccess("Account Search", null, "Accounts with the provided email:\n" + accountInfo.toString());
        }
        fetchEmailIdColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getId()));
        fetchEmailFirstNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFirstName()));
        fetchEmailLastNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastName()));
        fetchByEmailTable.setItems(accountList);
    }
    public static void showAlert(String title,  String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }
    public static void showSuccess(String infoMessage, String header, String title)    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }


    public void goToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error!", null, "Error loading login page.");
        }
    }

    /**
     public void addSongToAccount(ActionEvent event) {
     /**
     * song list view

     Song selectedSong = songListView.getSelectionModel().getSelectedItem();

     if (selectedSong != null) {
     currentAccount.getSongs().add(selectedSong);
     JavaPostgreSql.updateAccount(currentAccount);

     // Refresh the UI or show a confirmation message
     }
     }
     */
}