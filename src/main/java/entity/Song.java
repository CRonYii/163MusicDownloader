package entity;

import ui.*;
import util.Database;
import util.Downloader;
import util.ThreadUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Song extends DownloadableEntity implements Serializable {

    private static final long serialVersionUID = 501L;

    private static final List<String> columns = new ArrayList<>(Arrays.asList("Song Name", "Artist", "Album", "Action"));
    private static final List<PropertyDefinition> properties = new ArrayList<>(Arrays.asList(
            constProp("name").setCell(param -> new ClickableTreeTableCell(entity -> ((Song) entity).getName(),
                    keyword -> SearchService.create(keyword, new SearchEvent.KeywordSongSearchEvent()).load())
                    .width(0.4)),
            constProp("artist", "getArtistName").setCell(param -> new ClickableTreeTableCell(entity -> ((Song) entity).getArtist().getId(),
                    id -> SearchService.create(id, new SearchEvent.IdArtistSearchEvent()).load())
                    .width(0.20)),
            constProp("album", "getAlbumName").setCell(param -> new ClickableTreeTableCell(entity -> ((Song) entity).getAlbum().getId(),
                    id -> SearchService.create(id, new SearchEvent.IdAlbumSearchEvent()).load())
                    .width(0.28)),
            constProp("id").setCell(param -> new ClickableTreeTableCell(entity -> ((Song) entity).getId(),
                    id -> ThreadUtils.startThread(new ReadStringTask(id, new DownloadEvent.SongDownloadEvent())))
                    .width(0.1).setIsButton(true).setCustomName("Download"))
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
