package entity;

import ui.Center;
import util.Downloader;
import util.ElementNotFoundException;
import util.Spider;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {

    private static final long serialVersionUID = 503L;

    private final Artist artist;
    private final String name;
    private final String id;
    private List<Song> songList;

    public Album(Artist artist, String name, String id) {
        this(artist, name, id, new ArrayList<>());
    }

    public Album(Artist artist, String name, String id, List<Song> songList) {
        this.artist = artist;
        this.name = name;
        this.id = id;
        this.songList = songList;

        artist.addAlbum(this);
    }

    public void downloadAllSongs() {
        Downloader.downloader.downloadSong(getSongList());
        Center.printToStatus(String.format("playlist id: %s, all songs added to download list\n", id));
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

    public List<Song> getSongList() {
        if (songList == null) {
            try {
                fetchSongList();
            } catch (IOException | ElementNotFoundException e) {
                Center.toast(String.format("Failed to get Playlist %s's songs", name));
                e.printStackTrace();
            }
        }
        return songList;
    }

    private void fetchSongList() throws IOException, ElementNotFoundException {
        songList = Spider.getAlbumByID(id).songList;
    }

    public int size() {
        return getSongList().size();
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
