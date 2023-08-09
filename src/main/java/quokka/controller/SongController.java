package quokka.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import quokka.JavaPostgreSql;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import quokka.models.Account;
import quokka.models.Song;

import java.time.LocalDate;

public class SongController {
    @FXML
    public TextField song_title, song_artist, song_album;
    @FXML
    public TableView<Song> SongsTable;
    @FXML
    public DatePicker song_release_date;
    @FXML
    public TableColumn<Song, String> savedTitle, savedArtist, savedAlbum;

    @FXML
    public TableColumn<Song, LocalDate> savedReleaseDate;

    @FXML
    public Label welcomeMessage;

    private static Account currentAccount;


    public void showWelcome(){
        System.out.println(currentAccount.toString());

        welcomeMessage.setText("Welcome, " + currentAccount.getFirstName() + " " +
                currentAccount.getLastName() + "!" );


    }
    public void collectUser(Account account){
        currentAccount = account;
        updateSongsTableView();


    }


    public void writeData(ActionEvent event){
        int savedSongId = JavaPostgreSql.saveSong(song_title.getText(), song_artist.getText(), song_album.getText(), song_release_date.getValue());

        savedTitle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongTitle()));
        savedArtist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongArtist()));
        savedAlbum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongAlbum()));
        savedReleaseDate.setCellValueFactory(param -> new SimpleObjectProperty<LocalDate>(param.getValue().getReleaseDate()));

        song_album.clear();
        song_title.clear();
        song_artist.clear();
        song_release_date.getEditor().clear();

        SongsTable.setItems(JavaPostgreSql.getAllSongs());

    }

    public void addSelectedSongToAccount(ActionEvent event) {
        Song selectedSong = SongsTable.getSelectionModel().getSelectedItem();

        if (selectedSong != null && currentAccount != null) {
            // Associate the selected song with the logged-in account
            JavaPostgreSql.associateSongWithAccount(currentAccount, selectedSong);

            updateSongsTableView();
            AccountController.showSuccess("Song Added", "The selected song has been added to your account.", "Success");
        } else {
            // Show an error message if no song is selected or no account is logged in
            AccountController.showAlert("Error", "Please select a song and log in to add the song to your account.", "Error");
        }
    }

    private void updateSongsTableView() {
        ObservableList<Song> allSongs = JavaPostgreSql.getAllSongs();
        System.out.println(allSongs);
        SongsTable.setItems(allSongs);
    }
}
