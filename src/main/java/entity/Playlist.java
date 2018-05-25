package entity;

import ui.Center;
import util.Downloader;

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

    public void downloadAllSongs() {
        Downloader.getInstance().downloadSong(songList);
        Center.printToStatus(String.format("playlist %s, all songs added to download list\n", title));
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public int size() {
        return songList.size();
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", songList=" + songList +
                '}';
    }
}
