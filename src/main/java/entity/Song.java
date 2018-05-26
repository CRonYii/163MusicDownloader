package entity;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.TreeTableCell;
import ui.ReadStringTask;
import ui.StringParamEvent;
import util.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Song extends Entity implements Serializable {

    private static final long serialVersionUID = 501L;

    private static final List<String> columns = new ArrayList<>(Arrays.asList("Name", "Artist", "Album", "Action"));
    private static final List<PropertyDefinition> properties = new ArrayList<>(Arrays.asList(
            constProp("name"),
            constProp("artist", "getArtistName"),
            constProp("album", "getAlbumName"),
            constProp("id").setCell(param -> new TreeTableCell<Entity, String>() {
                @Override
                protected void updateItem(String id, boolean empty) {
                    if (!empty) {
                        Song song = (Song) this.getTableColumn().getTreeTableView().getTreeItem(this.getIndex()).getValue();
                        JFXButton button = new JFXButton("Download");
                        button.setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
                        button.setButtonType(JFXButton.ButtonType.RAISED);
                        button.setOnAction(event -> ThreadUtils.startThread(new ReadStringTask(id, new StringParamEvent.SongDownloadEvent())));
                        setGraphic(button);
                        setText("");
                    } else {
                        setGraphic(null);
                    }
                }
            })
    ));

    private final String name;
    private final String id;
    private Artist artist;
    private Album album;

    public Song(String id, String name, Artist artist, Album album) {
        this.id = id;
        this.name = Downloader.makeStringValidForWindowsFile(name);
        setArtist(artist);
        setAlbum(album);

        Database.addSong(this);
    }

    /**
     * Download the song with mp3 tags to the given directory.
     *
     * @param dir directory
     */
    public void download(File dir) {
        Downloader.downloader.downloadSong(Song.this, dir);
    }

    public void download() {
        download(Downloader.TEMP_DIR);
    }

    public void setArtistAndAlbum() {
        if (artist != null && album != null)
            return;
        try {
            Spider.setArtistAndAlbum(this);
        } catch (ElementNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getArtistName() {
        if (artist == null)
            return "";
        return artist.getName();
    }

    public String getAlbumName() {
        if (album == null)
            return "";
        return album.getName();
    }

    public void setArtist(Artist artist) {
        if (artist == null)
            return;
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        if (album == null)
            return;
        this.album = album;
    }

    public String getDownloadURL() {
        return Database.getSongDownloadURL(id);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artist=" + (artist != null ? artist.getName() : "null") +
                ", album=" + (album != null ? album.getName() : "null") +
                '}';
    }

    public boolean exists() {
        return new File(Database.database.getSongDir() + "\\" + getArtist().getName() + " - " + getName() + ".mp3").exists();
    }

    @Override
    public List<String> getColumns() {
        return columns;
    }

    @Override
    public List<PropertyDefinition> getProperties() {
        return properties;
    }
}
