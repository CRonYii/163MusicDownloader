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
                Center.printToStatus("Successfully read data from previous database...");
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

    public static Database getInstance() {
        return database;
    }

    public static Song getSong(String id) throws IOException, ElementNotFoundException {
        if (hasSong(id))
            return getInstance().songMap.get(id);
        Song song = Spider.getSongByID(id);
        addSong(song);
        return song;
    }

    public static void addSong(Song song) {
        getInstance().songMap.putIfAbsent(song.getId(), song);
    }

    public static boolean hasSong(String id) {
        return getInstance().songMap.containsKey(id);
    }

    public static Artist getArtist(String id) throws IOException, ElementNotFoundException {
        if (hasArtist(id))
            return getInstance().artistMap.get(id);
        Artist artist = Spider.getArtistByID(id);
        addArtist(artist);
        return artist;
    }

    public static void addArtist(Artist artist) {
        getInstance().artistMap.putIfAbsent(artist.getId(), artist);
    }

    public static boolean hasArtist(String id) {
        return getInstance().artistMap.containsKey(id);
    }

    public static Album getAlbum(String id) throws IOException, ElementNotFoundException {
        if (hasAlbum(id))
            return getInstance().albumMap.get(id);
        Album album = Spider.getAlbumByID(id);
        addAlbum(album);
        return album;
    }

    public static void addAlbum(Album album) {
        getInstance().albumMap.putIfAbsent(album.getId(), album);
    }

    public static boolean hasAlbum(String id) {
        return getInstance().albumMap.containsKey(id);
    }

    public static Playlist getPlaylist(String id) throws IOException, ElementNotFoundException {
        if (hasPlaylist(id))
            return getInstance().playlistMap.get(id);
        Playlist playlist = Spider.getPlaylistByID(id);
        addPlaylist(playlist);

        return playlist;
    }

    public static void addPlaylist(Playlist playlist) {
        getInstance().playlistMap.putIfAbsent(playlist.getId(), playlist);
    }

    public static boolean hasPlaylist(String id) {
        return getInstance().playlistMap.containsKey(id);
    }

    public File getSongDir() {
        return SONG_DIR;
    }

    public void setSongDir(File songDir) {
        SONG_DIR = songDir;
    }

}
