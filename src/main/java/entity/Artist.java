package entity;

import ui.Center;
import util.Database;
import util.Downloader;
import util.Spider;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Artist implements Serializable {

    private static final long serialVersionUID = 502L;

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

    public void downloadAllAlbum() {
        for (Album a : albumList) {
            a.downloadAllSongs();
        }
    }

    public void downloadAllSongs() {
        Downloader.downloader.downloadSong(getSongList());
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

}
