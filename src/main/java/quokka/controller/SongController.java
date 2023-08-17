package quokka.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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
import java.net.URL;
import java.time.LocalDate;

import static quokka.JavaPostgreSql.createSessionFactory;

public class SongController {

    private Stage stage;
    private Scene scene;

    boolean isRegClicked, isHomeClicked, isBrowseClicked = false;
    @FXML
    public Label welcomeMessage;


    @FXML
    private StackPane allSongsPane;
    @FXML
    private StackPane hitSongsPane;
    @FXML
    private StackPane likedSongsPane;

    @FXML
    TextField song_title, song_album, song_artist;
    @FXML
    DatePicker song_release_date;
    private SongTableController hitTable = new SongTableController();
    private SongTableController likedTable = new SongTableController();
    private SongTableController allSongsTable = new SongTableController();
    final TableColumn hitColumn = new TableColumn("Hit Count");


    private static Account currentAccount;



    // A label to display the current user's full name.
    public void showWelcome(){
        welcomeMessage.setText("Welcome, " + currentAccount.getFirstName() + " "
                                           + currentAccount.getLastName() + "!" );

    }
    // Passes the currently logged-in user between controllers. (From Login to Song)
    public void collectUser(Account account){
        currentAccount = account;
    }



    //Adds a new song to the system
    public void writeData(ActionEvent event){
        int savedSongId = JavaPostgreSql.saveSong(song_title.getText(), song_artist.getText(), song_album.getText(), song_release_date.getValue());
        Song song = new Song(song_title.getText(), song_artist.getText(), song_album.getText(), song_release_date.getValue());

        allSongsTable.setData(JavaPostgreSql.getAllSongs());
        allSongsTable.getColumns().add(song);

       // setColumns(browseTitle, browseArtist, browseAlbum, browseReleaseDate);

        song_album.clear();
        song_title.clear();
        song_artist.clear();
        song_release_date.getEditor().clear();

        allSongsTable.setItems(JavaPostgreSql.getAllSongs());

    }


    public void addSelectedSongToAccount(ActionEvent event) {
        Song selectedSong = (Song) allSongsTable.getSelectionModel().getSelectedItem();

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
                    // Increment hit count for the selected song
            //        song.setHitCount(song.getHitCount() + 1);
                    session.update(song);


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

      //      updateBrowseSongsTableView(); // Refresh the table view


        } else {
            AccountController.showAlert("Error", "Please select a song to add the song to your account.", "Error");
        }
    }


    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToSongRegister(ActionEvent event) throws IOException {
        if (!isRegClicked) {
            allSongsTable.setData(JavaPostgreSql.getAllSongs());
            try {
                allSongsPane.getChildren().add(allSongsTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRegClicked = true;
        }
        Parent root = FXMLLoader.load(getClass().getResource("SongRegisterPage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void goToBrowseSong(ActionEvent event) throws IOException {
        if (!isBrowseClicked) {
            allSongsTable.setData(JavaPostgreSql.getAllSongs());
            try {
                allSongsPane.getChildren().add(allSongsTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isBrowseClicked = true;
        }
        Parent root = FXMLLoader.load(getClass().getResource("BrowseSongsPage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void goToHome(ActionEvent event) throws IOException {
        if (!isHomeClicked) {
            hitTable.setData(JavaPostgreSql.getAllSongs());
            try {
                hitSongsPane.getChildren().add(hitTable);
            } catch (Exception e) {
                e.printStackTrace();
            }

            likedTable.setData(JavaPostgreSql.getMySongs(currentAccount));

            try {
                likedSongsPane.getChildren().add(likedTable);
            } catch (Exception e) {
                e.printStackTrace();
            }

            isHomeClicked = true;
        }
        Parent root = FXMLLoader.load(getClass().getResource("HomePage.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
