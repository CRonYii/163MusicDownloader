package util;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Spider {

    private static final String PLAYLIST_URL = "http://music.163.com/weapi/playlist/detail";
    private static final String SONG_URL = "http://music.163.com/weapi/song/detail";
    private static final String ALBUM_URL = "http://music.163.com/weapi/v1/album/";
    private static final String ARTIST_URL = "http://music.163.com/weapi/v1/artist/";
    private static final String SEARCH_URL = "http://music.163.com/weapi/search/get";
    private static final String DOWNLOADER_URL = "https://ouo.us/fm/163/";

    /* The number of albums to display in one page, set to 1000 because want all albums at once */
    private static final String DISPLAY_LIMIT = "1000";
    private static final String TYPE_SONG = "1";
    private static final String TYPE_ALBUM = "10";
    private static final String TYPE_ARTIST = "100";
    private static final String TYPE_PLAYLIST = "1000";
    private static final int LIMIT = 30;

    private static final String[] userAgents = {
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89;GameHelper",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:46.0) Gecko/20100101 Firefox/46.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:46.0) Gecko/20100101 Firefox/46.0",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
            "Mozilla/5.0 (Windows NT 6.3; Win64, x64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/13.10586",
            "Mozilla/5.0 (iPad; CPU OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1"
    };

    private static String randomAgent() {
        return userAgents[(int) (Math.random() * userAgents.length)];
    }

    private static Connection get163Connection(String url) {
        return Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4")
                .header("Cache-Control", "no-cache")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Connection", "keep-alive")
                .header("Cookie", "hblid=u7yqEzYcj6AuNKIu3m39N0X92JE81Tb6; olfsk=olfsk30323116945761863; Phpstorm-43fc28d3=5ded32b9-e416-4a13-a274-93220dfa8f77; Pycharm-156b5fb8=410856b9-877b-484b-9685-1cf433d61246; cookieName=AEXCookie; Webstorm-ca7b1b10=56dffa0f-081a-47ed-842a-2a3c3de18f48; _ga=GA1.1.7478905.1526694087")
                .header("DNT", "1")
                .header("Host", "music.163.com")
                .header("Pragma", "no-cache")
                .header("Referer", "http://music.163.com")
                .header("Upgrade-Insecure-Requests", "1")
                .userAgent(randomAgent());
    }

    public static String getSongDownloadURL(String songID) throws IOException {
        Element body = get163Connection(DOWNLOADER_URL)
                .data("id", songID)
                .get().body();

        return "http:" + body.select("a[class=button]").get(1).attr("href");
    }

    public static Playlist getPlaylistByID(String playlistId) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("id", playlistId);
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(PLAYLIST_URL);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getPlaylist(data);
    }

    public static Song getSongByID(String songId) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("ids", "[" + songId + "]");
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(SONG_URL);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getSongDetail(data);
    }

    public static Album getAlbumByID(String albumId) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(ALBUM_URL + albumId);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getAlbumDetail(data);
    }

    public static Artist getArtistByID(String artistId) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(ARTIST_URL + artistId);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getArtistDetail(data);
    }

    public static List<Song> searchSong(String keyword, int offset) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("s", keyword);
        jsonInput.put("type", TYPE_SONG);
        jsonInput.put("offset", offset);
        jsonInput.put("limit", LIMIT);
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(SEARCH_URL);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getSongList(data);
    }

    public static List<Playlist> searchPlaylist(String keyword, int offset) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("s", keyword);
        jsonInput.put("type", TYPE_PLAYLIST);
        jsonInput.put("offset", offset);
        jsonInput.put("limit", LIMIT);
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(SEARCH_URL);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getPlaylistList(data);
    }

    public static List<Artist> searchArtist(String keyword, int offset) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("s", keyword);
        jsonInput.put("type", TYPE_ARTIST);
        jsonInput.put("offset", offset);
        jsonInput.put("limit", LIMIT);
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(SEARCH_URL);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getArtistList(data);
    }

    public static List<Album> searchAlbum(String keyword, int offset) throws IOException {
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("s", keyword);
        jsonInput.put("type", TYPE_ALBUM);
        jsonInput.put("offset", offset);
        jsonInput.put("limit", LIMIT);
        jsonInput.put("csrf_token", "");

        Map<String, String> params = EncryptUtils.encrypt(jsonInput.toString());
        Connection connection = get163Connection(SEARCH_URL);
        Connection.Response response = connection.data(params)
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .execute();

        JSONObject data = JSONObject.fromObject(response.body());
        return DataParser.getAlbumList(data);
    }

}