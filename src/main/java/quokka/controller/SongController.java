package quokka.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.hibernate.envers.internal.entities.mapper.relation.query.TwoEntityOneAuditedQueryGenerator;
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


    public void showWelcome(){
        System.out.println(currentAccount.toString());

        welcomeMessage.setText("Welcome, " + currentAccount.getFirstName() + " " +
                currentAccount.getLastName() + "!" );


    }
    public void collectUser(Account account){
        currentAccount = account;
        updateBrowseSongsTableView();


    }

    public void initialize(){
        browseTitle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongTitle()));
        browseArtist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongArtist()));
        browseAlbum.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongAlbum()));
        browseReleaseDate.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getReleaseDate()));

    }

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
            // Associate the selected song with the logged-in account
            JavaPostgreSql.associateSongWithAccount(currentAccount, selectedSong);

            updateBrowseSongsTableView();
            AccountController.showSuccess("Song Added", "The selected song has been added to your account.", "Success");
        } else {
            // Show an error message if no song is selected or no account is logged in
            AccountController.showAlert("Error", "Please select a song and log in to add the song to your account.", "Error");
        }
    }

    private void updateBrowseSongsTableView() {
        ObservableList<Song> allSongs = JavaPostgreSql.getAllSongs();
        System.out.println(allSongs);
        BrowseSongsTable.setItems(allSongs);
    }

}
