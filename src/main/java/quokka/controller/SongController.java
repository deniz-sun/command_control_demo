package quokka.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import quokka.JavaPostgreSql;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import quokka.models.Account;
import quokka.models.Song;

import java.io.IOException;
import java.time.LocalDate;

import static quokka.JavaPostgreSql.createSessionFactory;

public class SongController {
    private Stage stage;
    private Scene scene;

    @FXML
    public TextField song_title, song_artist, song_album;
    @FXML
    public TableView<Song> SongsTable;
    @FXML
    public TableView<Song> BrowseSongsTable;
    @FXML
    public DatePicker song_release_date;
    @FXML
    public TableColumn<Song, String> savedTitle, savedArtist, savedAlbum;
    @FXML
    public TableColumn<Song, String> browseTitle, browseAlbum, browseArtist;


    @FXML
    public TableColumn<Song, LocalDate> savedReleaseDate;
    @FXML
    public TableColumn<Song, LocalDate> browseReleaseDate;

    @FXML
    public Label welcomeMessage;

    private static Account currentAccount;

    // A label to display the current user's full name.
    public void showWelcome(){
        welcomeMessage.setText("Welcome, " + currentAccount.getFirstName() + " "
                                           + currentAccount.getLastName() + "!" );


    }
    // Passes the currently logged-in user between controllers. (From Login to Song)
    public void collectUser(Account account){
        currentAccount = account;
        updateBrowseSongsTableView();


    }
    // Called on default, sets the table elements for all songs in the system.
    public void initialize(){
        browseTitle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongTitle()));
        browseArtist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongArtist()));
        browseAlbum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongAlbum()));
        browseReleaseDate.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getReleaseDate()));



    }

    //Adds a new song to the system
    public void writeData(ActionEvent event){
        int savedSongId = JavaPostgreSql.saveSong(song_title.getText(), song_artist.getText(), song_album.getText(), song_release_date.getValue());

        savedTitle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongTitle()));
        savedArtist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongArtist()));
        savedAlbum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongAlbum()));
        savedReleaseDate.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getReleaseDate()));

        song_album.clear();
        song_title.clear();
        song_artist.clear();
        song_release_date.getEditor().clear();

        SongsTable.setItems(JavaPostgreSql.getAllSongs());

    }

    public void addSelectedSongToAccount(ActionEvent event) {
        Song selectedSong = BrowseSongsTable.getSelectionModel().getSelectedItem();

        if (selectedSong != null && currentAccount != null) {
            SessionFactory factory = createSessionFactory();
            Session session = factory.openSession();
            Transaction transaction = null;

            try {
                transaction = session.beginTransaction();

                // Retrieve the current account and selected song
                Account account = session.get(Account.class, currentAccount.getId());
                Song song = session.get(Song.class, selectedSong.getId());

                // Check if the selected song is already added to the user's account
                if (!account.getSongs().contains(song)) {
                    // Associate the selected song with the logged-in account
                    account.getSongs().add(song);
                    session.update(account);

                    transaction.commit();
                    AccountController.showSuccess("Song Added", "The selected song has been added to your account.", "Success");
                } else {
                    AccountController.showAlert("Song Already Added", "The selected song is already added to your account.", "Info");
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                AccountController.showAlert("Error", "An error occurred while adding the song to your account.", "Error");
            } finally {
                session.close();
                factory.close();
            }

            updateBrowseSongsTableView(); // Refresh the table view

        } else {
            AccountController.showAlert("Error", "Please select a song and log in to add the song to your account.", "Error");
        }
    }




    private void updateBrowseSongsTableView() {
        ObservableList<Song> allSongs = JavaPostgreSql.getAllSongs();
        System.out.println(allSongs);
        BrowseSongsTable.setItems(allSongs);
    }

    public void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToSongRegister(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SongRegisterPage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
