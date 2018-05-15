package util;

import entity.Song;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Set;

public class DataParser {

    public static Set<Song> getSongList(JSONObject data) {
        if ((int) data.get("code") != 200)
            return null;
        JSONObject result = data.getJSONObject("result");
        JSONArray songs = result.getJSONArray("songs");
        for (int i = 0; i < songs.size(); i++) {
            JSONObject song = songs.getJSONObject(i);
            System.out.println(song);
        }
        return null;
    }

}
