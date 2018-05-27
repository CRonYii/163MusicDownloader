package entity;

import ui.*;
import util.Database;
import util.Downloader;
import util.Spider;
import util.ThreadUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Album extends DownloadableEntity implements Serializable {

    private static final long serialVersionUID = 503L;

    private static final List<String> columns = new ArrayList<>(Arrays.asList("Album Name", "Artist", "Action"));
    private static final List<PropertyDefinition> properties = new ArrayList<>(Arrays.asList(
            constProp("name").setCell(param -> new ClickableTreeTableCell(entity -> ((Album) entity).getId(),
                    id -> SearchService.create(id, new SearchEvent.IdAlbumSearchEvent()).load())),
            constProp("artist", "getArtistName").setCell(param -> new ClickableTreeTableCell(entity -> ((Album) entity).getArtist().getId(),
                    id -> SearchService.create(id, new SearchEvent.IdArtistSearchEvent()).load())),
            constProp("id").setCell(param -> new ClickableTreeTableCell(entity -> ((Album) entity).getId(),
                    id -> ThreadUtils.startThread(new ReadStringTask(id, new DownloadEvent.AlbumDownloadEvent())))
                    .setIsButton(true).setCustomName("Download"))
    ));

    private final String name;
    private final String id;
    private final Artist artist;
    private List<Song> songList;

    public Album(Artist artist, String name, String id) {
        this(artist, name, id, null);
    }

    public Album(Artist artist, String name, String id, List<Song> songList) {
        this.artist = artist;
        this.name = name;
        this.id = id;
        this.songList = songList;

        artist.addAlbum(this);
    }

    public void download() {
        Downloader.downloader.downloadSong(getSongList());
        Center.toast("Downloading all songs of Playlist " + name);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getArtistName() {
        if (artist == null)
            return "";
        return artist.getName();
    }

    public List<Song> getSongList() {
        if (songList == null || songList.isEmpty()) {
            try {
                fetchSongList();
                Database.addAlbum(this);
            } catch (IOException e) {
                Center.toast(String.format("Failed to get Playlist %s's songs", name));
                e.printStackTrace();
            }
        }
        return songList;
    }

    private void fetchSongList() throws IOException {
        songList = Spider.getAlbumByID(id).songList;
    }

    public int size() {
        return getSongList().size();
    }

    @Override
    public List<String> getColumns() {
        return columns;
    }

    @Override
    public List<PropertyDefinition> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Album{" +
                "artist=" + artist.getName() +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
