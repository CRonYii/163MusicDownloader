package util;

import entity.Album;
import entity.Artist;
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
        List<Song> songSet = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            JSONObject song = songs.getJSONObject(i);
            songSet.add(getSong(song));
        }
        return songSet;
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
