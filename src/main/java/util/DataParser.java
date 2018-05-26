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
//            playlistList.add(Spider.getPlaylistByID(playlist.getString("id")));
            playlistList.add(new Playlist(playlist.getString("id"), playlist.getString("name")));
        }
        return playlistList;
    }

    public static List<Artist> getArtistList(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("result");
        JSONArray artists = result.getJSONArray("artists");
        List<Artist> artistList = new ArrayList<>();
        for (int i = 0; i < artists.size(); i++) {
            JSONObject artist = artists.getJSONObject(i);
            artistList.add(getArtist(artist));
        }
        return artistList;
    }

    public static List<Album> getAlbumList(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("result");
        JSONArray albums = result.getJSONArray("albums");
        List<Album> albumList = new ArrayList<>();
        for (int i = 0; i < albums.size(); i++) {
            JSONObject album = albums.getJSONObject(i);
            albumList.add(getAlbum(album, getArtist(album.getJSONObject("artist"))));
        }
        return albumList;
    }

    public static Playlist getPlaylist(JSONObject data) {
        JSONObject result = data.getJSONObject("result");
        JSONArray tracks = result.getJSONArray("tracks");
        List<Song> songList = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            JSONObject song = tracks.getJSONObject(i);
            songList.add(getSong(song));
        }
        return new Playlist(result.getString("id"), result.getString("name"), songList);
    }

    public static Song getSong(JSONObject data) {
        Artist artist = getArtist(data.getJSONArray("artists").getJSONObject(0));
        Album album = getAlbum(data.getJSONObject("album"), artist);
        return new Song(data.getString("id"), data.getString("name"), artist, album);
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

    public static Song getSongDetail(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        return getSong(data.getJSONArray("songs").getJSONObject(0));
    }

}
