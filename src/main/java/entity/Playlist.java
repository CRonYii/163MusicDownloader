package entity;

import ui.Center;
import util.Downloader;
import util.Spider;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {

    private static final long serialVersionUID = 504L;

    private final String id;
    private final String title;
    private List<Song> songList;

    public Playlist(String id, String title, List<Song> songList) {
        this.id = id;
        this.title = title;
        this.songList = songList;
    }

    public Playlist(String id, String title) {
        this(id, title, null);
    }

    public void downloadAllSongs() {
        Downloader.downloader.downloadSong(getSongList());
        Center.toast(String.format("playlist %s, all songs added to download list\n", title));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Song> getSongList() {
        if (songList == null) {
            try {
                fetchSongList();
            } catch (IOException e) {
                Center.toast(String.format("Failed to get Playlist %s's songs", title));
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
                ", title='" + title + '\'' +
                '}';
    }
}
