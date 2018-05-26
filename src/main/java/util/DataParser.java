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

// TODO: instead of returning Null, throw an exception or toast an error message
public class DataParser {

    public static List<Song> getSongList(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("result");
        List<Song> songList = new ArrayList<>();
        if (result.getInt("songCount") == 0)
            return songList;
        JSONArray songs = result.getJSONArray("songs");
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
        List<Playlist> playlistList = new ArrayList<>();
        if (result.getInt("playlistCount") == 0)
            return playlistList;
        JSONArray playlists = result.getJSONArray("playlists");
        for (int i = 0; i < playlists.size(); i++) {
            JSONObject playlist = playlists.getJSONObject(i);
            playlistList.add(new Playlist(playlist.getString("id"), playlist.getString("name")));
        }
        return playlistList;
    }

    public static List<Artist> getArtistList(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("result");
        List<Artist> artistList = new ArrayList<>();
        if (result.getInt("artistCount") == 0)
            return artistList;
        JSONArray artists = result.getJSONArray("artists");
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
        List<Album> albumList = new ArrayList<>();
        if (result.getInt("albumCount") == 0)
            return albumList;
        JSONArray albums = result.getJSONArray("albums");
        for (int i = 0; i < albums.size(); i++) {
            JSONObject album = albums.getJSONObject(i);
            albumList.add(getAlbum(album, getArtist(album.getJSONObject("artist"))));
        }
        return albumList;
    }

    public static Album getAlbumDetail(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("album");
        Artist artist = getArtist(result.getJSONArray("artists").getJSONObject(0));

        List<Song> songList = new ArrayList<>();
        Album album = new Album(artist, result.getString("name"), result.getString("id"), songList);
        JSONArray songs = data.getJSONArray("songs");
        for (int i = 0; i < songs.size(); i++) {
            JSONObject song = songs.getJSONObject(i);
            songList.add(new Song(song.getString("id"), song.getString("name"), artist, album));
        }
        return album;
    }

    public static Artist getArtistDetail(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("artist");

        List<Song> songList = new ArrayList<>();
        Artist artist = new Artist(result.getString("name"), result.getString("id"), new ArrayList<>(), songList);
        JSONArray songs = data.getJSONArray("hotSongs");
        for (int i = 0; i < songs.size(); i++) {
            JSONObject song = songs.getJSONObject(i);
            JSONObject albumJson = song.getJSONObject("al");
            String albumId = albumJson.getString("id");
            String albumName = albumJson.getString("name");
            Album album = null;
            try {
                album = Database.hasAlbum(albumId) ? Database.getAlbum(albumId) : new Album(artist, albumName, albumId);
            } catch (IOException e) {
                // unreachable exception, leave it like this
            }
            songList.add(new Song(song.getString("id"), song.getString("name"), artist, album));
        }
        return artist;
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
