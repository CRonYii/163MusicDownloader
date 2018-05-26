package entity;

import ui.Center;
import util.Database;
import util.Downloader;
import util.Spider;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {

    private static final long serialVersionUID = 504L;

    private final String id;
    private final String name;
    private List<Song> songList;

    public Playlist(String id, String name, List<Song> songList) {
        this.id = id;
        this.name = name;
        this.songList = songList;
    }

    public Playlist(String id, String name) {
        this(id, name, null);
    }

    public void downloadAllSongs() {
        Downloader.downloader.downloadSong(getSongList());
        Center.toast(String.format("playlist %s, all songs added to download list\n", name));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Song> getSongList() {
        if (songList == null || songList.isEmpty()) {
            try {
                fetchSongList();
                Database.addPlaylist(this);
            } catch (IOException e) {
                Center.toast(String.format("Failed to get Playlist %s's songs", name));
                e.printStackTrace();
            }
        }
        return songList;
    }

    private void fetchSongList() throws IOException {
        songList = Spider.getPlaylistByID(id).songList;
    }

    public int size() {
        return getSongList().size();
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
