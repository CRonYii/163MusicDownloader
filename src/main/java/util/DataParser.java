package util;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// TODO: instead of return null, throw an SearchException
public class DataParser {

    public static List<Song> getSongList(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("result");
        JSONArray songs = result.getJSONArray("songs");
        List<Song> songList = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            JSONObject song = songs.getJSONObject(i);
            songList.add(getSong(song));
        }
        return songList;
    }

    public static List<Playlist> getPlaylistList(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("result");
        JSONArray playlists = result.getJSONArray("playlists");
        List<Playlist> playlistList = new ArrayList<>();
        for (int i = 0; i < playlists.size(); i++) {
            JSONObject playlist = playlists.getJSONObject(i);
            playlistList.add(getPlaylist(playlist));
        }
        return playlistList;
    }

    public static Playlist getPlaylist(JSONObject data) {
        try {
            return Database.getPlaylist(data.getString("id"));
        } catch (IOException | ElementNotFoundException e) {
            return new Playlist(data.getString("id"), data.getString("name"), new ArrayList<>());
        }
    }

    public static Song getSong(JSONObject data) {
        Artist artist = getArtist(data.getJSONArray("artists").getJSONObject(0));
        return new Song(data.getString("id"), data.getString("name"), artist, getAlbum(data.getJSONObject("album"), artist));
    }

    public static Artist getArtist(JSONObject data) {
        return new Artist(data.getString("name"), data.getString("id"));
    }

    public static Album getAlbum(JSONObject data, Artist artist) {
        return new Album(artist, data.getString("name"), data.getString("id"));
    }

    public static JSONObject readFromFile(String path) throws IOException {
        return JSONObject.fromObject(readFile(new File(path)));
    }

    public static JSONObject readFromFile(File file) throws IOException {
        return JSONObject.fromObject(readFile(file));
    }

    public static String readFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded);
    }

}
