package util;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import ui.Center;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Database implements Serializable {


    public static final File OUTPUT = new File("./database.ser");
    private static final long serialVersionUID = 500L;

    public static final Database database = init();
    private final Map<String, Song> songMap = new HashMap<>();
    private final Map<String, Artist> artistMap = new HashMap<>();
    private final Map<String, Album> albumMap = new HashMap<>();
    private final Map<String, Playlist> playlistMap = new HashMap<>();
    private final Map<String, String> songDownloadMap = new HashMap<>();

    private File SONG_DIR = new File("songs/");

    private int maxConcurrentDownload = 5;
    private int failConnectionWaitTime = 15;
    private int reconnectionTimes = 3;

    private Database() {
    }

    private static Database init() {
        if (OUTPUT.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(OUTPUT));
                Object obj = in.readObject();
                Center.toast("Successfully read data from previous database...");
                return (Database) obj;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new Database();
    }

    public int getMaxConcurrentDownload() {
        return maxConcurrentDownload;
    }

    public void setMaxConcurrentDownload(int maxConcurrentDownload) {
        this.maxConcurrentDownload = maxConcurrentDownload;
    }

    public int getFailConnectionWaitTime() {
        return failConnectionWaitTime;
    }

    public void setFailConnectionWaitTime(int failConnectionWaitTime) {
        this.failConnectionWaitTime = failConnectionWaitTime;
    }

    public int getReconnectionTimes() {
        return reconnectionTimes;
    }

    public void setReconnectionTimes(int reconnectionTimes) {
        this.reconnectionTimes = reconnectionTimes;
    }

    public static String setSongDownloadURL(String id, int tried) {
        String url = null;
        try {
            url = Spider.getSongDownloadURL(id);
            addSongDownloadURL(id, url);
        } catch (IOException e) {
            Database data = Database.database;
            if (tried < data.getReconnectionTimes()) {
                System.err.printf("Failed to get Download URL, will try again in %s second, id: %s\n", data.getFailConnectionWaitTime(), id);
                try {
                    Thread.sleep(data.getFailConnectionWaitTime() * 1000);
                } catch (InterruptedException e1) {
                    // Let it go
                }
                setSongDownloadURL(id, tried + 1);
            } else {
                Center.toast("Failed to get Download URL From ouo.us, give up, id: " + id);
                System.err.println("Failed to get Download URL From ouo.us, give up, id: " + id);
            }
        }
        return url;
    }

    public static String getSongDownloadURL(String id) {
        if (hasSongDownloadURL(id))
            return database.songDownloadMap.get(id);
        return setSongDownloadURL(id, 0);
    }

    public static void addSongDownloadURL(String id, String url) {
        database.songDownloadMap.putIfAbsent(id, url);
    }

    public static boolean hasSongDownloadURL(String id) {
        return database.songDownloadMap.containsKey(id);
    }

    public static Song getSong(String id) throws IOException {
        if (hasSong(id))
            return database.songMap.get(id);
        Song song = Spider.getSongByID(id);
        addSong(song);
        return song;
    }

    public static void addSong(Song song) {
        database.songMap.putIfAbsent(song.getId(), song);
    }

    public static boolean hasSong(String id) {
        return database.songMap.containsKey(id);
    }

    public static Artist getArtist(String id) throws IOException, ElementNotFoundException {
        if (hasArtist(id))
            return database.artistMap.get(id);
        Artist artist = Spider.getArtistByID(id);
        addArtist(artist);
        return artist;
    }

    public static void addArtist(Artist artist) {
        database.artistMap.putIfAbsent(artist.getId(), artist);
    }

    public static boolean hasArtist(String id) {
        return database.artistMap.containsKey(id);
    }

    public static Album getAlbum(String id) throws IOException, ElementNotFoundException {
        if (hasAlbum(id))
            return database.albumMap.get(id);
        Album album = Spider.getAlbumByID(id);
        addAlbum(album);
        return album;
    }

    public static void addAlbum(Album album) {
        database.albumMap.putIfAbsent(album.getId(), album);
    }

    public static boolean hasAlbum(String id) {
        return database.albumMap.containsKey(id);
    }

    public static Playlist getPlaylist(String id) throws IOException {
        if (hasPlaylist(id))
            return database.playlistMap.get(id);
        Playlist playlist = Spider.getPlaylistByID(id);
        addPlaylist(playlist);

        return playlist;
    }

    public static void addPlaylist(Playlist playlist) {
        database.playlistMap.putIfAbsent(playlist.getId(), playlist);
    }

    public static boolean hasPlaylist(String id) {
        return database.playlistMap.containsKey(id);
    }

    public File getSongDir() {
        return SONG_DIR;
    }

    public void setSongDir(File songDir) {
        SONG_DIR = songDir;
    }

}
