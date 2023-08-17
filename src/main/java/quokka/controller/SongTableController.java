package quokka.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import quokka.JavaPostgreSql;
import quokka.models.Song;

import java.time.LocalDate;

public class SongTableController extends TableView {
    LocalDate date;
    Song song = new Song("songname", "artist", "album", date);

    public SongTableController() {
        FXMLLoader loader = new FXMLLoader();
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load(getClass().getResourceAsStream("/quokka/controller/SongTable.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeColumns();
    }

    private void initializeColumns() {
        TableColumn<Song, String> title = new TableColumn<>("title");
        title.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongTitle()));

        TableColumn<Song, String> artist = new TableColumn<>("artist");
        artist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongArtist()));

        TableColumn<Song, String> album = new TableColumn<>("album");
        album.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSongAlbum()));

        TableColumn<Song, LocalDate> releaseDate = new TableColumn<>("release date");
        releaseDate.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getReleaseDate()));

        getColumns().addAll(title, artist, album, releaseDate);
    }
    public void setData(ObservableList<Song> songs) {
        setItems(songs);
    }
}

