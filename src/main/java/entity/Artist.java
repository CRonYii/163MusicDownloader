package entity;

import ui.Center;
import ui.ClickableTreeTableCell;
import ui.StringParamEvent;
import util.Database;
import util.Downloader;
import util.Spider;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Artist extends DownloadableEntity implements Serializable {

    private static final long serialVersionUID = 502L;

    private static final List<String> columns = new ArrayList<>(Arrays.asList("Artist Name", "Action"));
    private static final List<PropertyDefinition> properties = new ArrayList<>(Arrays.asList(
            constProp("name").setCell(param -> new ClickableTreeTableCell(entity -> ((Artist) entity).getId(), new StringParamEvent.IdArtistSearchEvent())),
            constProp("id").setCell(param -> new ClickableTreeTableCell(entity -> ((Artist) entity).getId(), new StringParamEvent.ArtistDownloadEvent()).setIsButton(true).setCustomName("Download"))
    ));

    private final String name;
    private final String id;
    private final List<Album> albumList;
    private List<Song> songList;

    public Artist(String name, String id) {
        this(name, id, new ArrayList<>(), null);
    }

    public Artist(String name, String id, List<Album> albumList, List<Song> songList) {
        this.name = name;
        this.id = id;
        this.albumList = albumList;
        this.songList = songList;
    }

    public void addAlbum(Album album) {
        albumList.add(album);
    }

    public void download() {
        Downloader.downloader.downloadSong(getSongList());
        Center.toast("Downloading all songs of Artist " + name);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public List<Song> getSongList() {
        if (songList == null || songList.isEmpty()) {
            try {
                fetchSongList();
                Database.addArtist(this);
            } catch (IOException e) {
                Center.toast(String.format("Failed to get Playlist %s's songs", name));
                e.printStackTrace();
            }
        }
        return songList;
    }

    private void fetchSongList() throws IOException {
        songList = Spider.getArtistByID(id).getSongList();
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
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
