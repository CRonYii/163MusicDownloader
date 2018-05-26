package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.Database;
import util.Downloader;
import util.ElementNotFoundException;
import util.Spider;

import java.io.File;
import java.io.Serializable;

public class Song extends RecursiveTreeObject<Song> implements Serializable {

    private static final long serialVersionUID = 501L;

    private final String id;
    private final String title;
    private Artist artist;
    private Album album;

    private transient StringProperty IDProperty;
    private transient StringProperty titleProperty;
    private transient StringProperty artistName;
    private transient StringProperty albumName;

    public Song(String id, String title, Artist artist, Album album) {
        this.id = id;
        this.title = Downloader.makeStringValidForWindowsFile(title);
        setArtist(artist);
        setAlbum(album);

        this.titleProperty = new SimpleStringProperty(this.title);
        this.IDProperty = new SimpleStringProperty(this.id);

        if (album != null)
            album.addSong(this);
        Database.addSong(this);
    }

    public Song(String id, String title) {
        this(id, title, null, null);
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

    public void setProperty() {
        setIDProperty();
        setTitleProperty();
        setArtistProperty();
        setAlbumProperty();
    }

    private void setIDProperty() {
        if (IDProperty == null)
            this.IDProperty = new SimpleStringProperty(id);
    }

    private void setTitleProperty() {
        if (titleProperty == null)
            this.titleProperty = new SimpleStringProperty(title);
    }

    private void setArtistProperty() {
        if (artistName == null && artist != null)
            this.artistName = new SimpleStringProperty(artist.getName());
    }

    private void setAlbumProperty() {
        if (albumName == null && album != null)
            this.albumName = new SimpleStringProperty(album.getName());
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleProperty() {
        return titleProperty.get();
    }

    public StringProperty titlePropertyProperty() {
        return titleProperty;
    }

    public StringProperty artistNameProperty() {
        return artistName;
    }

    public StringProperty IDPropertyProperty() {
        return IDProperty;
    }

    public StringProperty albumNameProperty() {
        return albumName;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        if (artist == null)
            return;
        this.artist = artist;
        this.artistName = new SimpleStringProperty(this.artist.getName());
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        if (album == null)
            return;
        this.album = album;
        album.addSong(this);
        this.albumName = new SimpleStringProperty(this.album.getName());
    }

    public String getDownloadURL() {
        return Database.getSongDownloadURL(id);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist=" + (artist != null ? artist.getName() : "null") +
                ", album=" + (album != null ? album.getName() : "null") +
                '}';
    }

    public boolean exists() {
        return new File(Database.database.getSongDir() + "\\" + getArtist().getName() + " - " + getTitleProperty() + ".mp3").exists();
    }
}
