package util;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ui.Center;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Spider {

    private static final String PLAYLIST_URL = "http://music.163.com/weapi/playlist/detail";
    private static final String SONG_URL = "http://music.163.com/weapi/song/detail";
    private static final String ALBUM_URL = "http://music.163.com/album";
    private static final String ARTIST_URL = "http://music.163.com/artist/album";
    private static final String SEARCH_URL = "http://music.163.com/weapi/search/get";
    private static final String DOWNLOADER_URL = "https://ouo.us/fm/163/";

    /* The number of albums to display in one page, set to 1000 because want all albums at once */
    private static final String DISPLAY_LIMIT = "1000";
    public static final String TYPE_SONG = "1";
    public static final String TYPE_ALBUM = "10";
    public static final String TYPE_ARTIST = "100";
    public static final String TYPE_PLAYLIST = "1000";
    public static final int LIMIT = 30;

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
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("Cookie", "_ntes_nnid=7eced19b27ffae35dad3f8f2bf5885cd,1476521011210; _ntes_nuid=7eced19b27ffae35dad3f8f2bf5885cd; usertrack=c+5+hlgB7TgnsAmACnXtAg==; Province=025; City=025; _ga=GA1.2.1405085820.1476521280; NTES_PASSPORT=6n9ihXhbWKPi8yAqG.i2kETSCRa.ug06Txh8EMrrRsliVQXFV_orx5HffqhQjuGHkNQrLOIRLLotGohL9s10wcYSPiQfI2wiPacKlJ3nYAXgM; P_INFO=hourui93@163.com|1476523293|1|study|11&12|jis&1476511733&mail163#jis&320100#10#0#0|151889&0|g37_client_check&mailsettings&mail163&study&blog|hourui93@163.com; JSESSIONID-WYYY=189f31767098c3bd9d03d9b968c065daf43cbd4c1596732e4dcb471beafe2bf0605b85e969f92600064a977e0b64a24f0af7894ca898b696bd58ad5f39c8fce821ec2f81f826ea967215de4d10469e9bd672e75d25f116a9d309d360582a79620b250625859bc039161c78ab125a1e9bf5d291f6d4e4da30574ccd6bbab70b710e3f358f%3A1476594130342; _iuqxldmzr_=25; __utma=94650624.1038096298.1476521011.1476588849.1476592408.6; __utmb=94650624.11.10.1476592408; __utmc=94650624; __utmz=94650624.1476521011.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)")
                .header("DNT", "1")
                .header("Host", "music.163.com")
                .header("Pragma", "no-cache")
                .header("Referer", "http,//music.163.com/")
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

    public static Album getAlbumByID(String albumID) throws IOException, ElementNotFoundException {
        List<Song> songList = new ArrayList<>();
        Element body = get163Connection(ALBUM_URL)
                .data("id", albumID)
                .get().body();

        Element eleAlbumTitle = body.selectFirst("div[class=tit]").selectFirst("h2[class=f-ff2]");
        if (eleAlbumTitle == null)
            throw new ElementNotFoundException("cannot find album title, id: " + albumID);

        Element eleArtist = body.selectFirst("p[class=intr]").selectFirst("a[class=s-fc7]");
        if (eleArtist == null)
            throw new ElementNotFoundException("cannot find artist, id: " + albumID);
        Artist artist = new Artist(eleArtist.text(), eleArtist.attr("href").substring(11));
        if (Database.hasArtist(artist.getId()))
            artist = Database.getArtist(artist.getId());

        Element eleListDetail = body.selectFirst("ul[class=f-hide]");
        if (eleListDetail == null)
            throw new ElementNotFoundException("invalid playlist id, id: " + albumID);
        Elements eleSongList = eleListDetail.select("a[href]");
        if (eleSongList.size() == 0)
            throw new ElementNotFoundException("Unable to get playlist, id: " + albumID);

        Album album = new Album(artist, eleAlbumTitle.text(), albumID, songList);
        for (Element song : eleSongList) {
            Song temp = new Song(song.attr("href").substring(9), song.text());
            temp.setArtist(artist);
            temp.setAlbum(album);
            songList.add(temp);
        }
        return album;
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

    public static Artist getArtistByID(String artistID) throws IOException, ElementNotFoundException {
        Artist artist;
        Element body = get163Connection(ARTIST_URL)
                .data("id", artistID)
                .data("limit", DISPLAY_LIMIT)
                .get().body();

        Element artistName = body.selectFirst("h2[id=artist-name]");
        if (artistName == null)
            throw new ElementNotFoundException("Unable to get artist, id: " + artistID);

        List<Album> albumList = new ArrayList<>();
        Elements eleInfo = body.selectFirst("ul[class=m-cvrlst m-cvrlst-alb4 f-cb]").select("a[class=icon-play f-alpha]");
        if (eleInfo == null)
            throw new ElementNotFoundException("Unable to get albums, id: " + artistID);
        for (Element e : eleInfo) {
            String albumID = e.attr("data-res-id");
            albumList.add(Database.getAlbum(albumID));
        }

        artist = new Artist(artistName.text(), artistID, albumList);

        Center.printToStatus("retrieved data for artist " + artist.getName());

        return artist;
    }

    public static void setArtistAndAlbum(Song song) throws ElementNotFoundException {
        if (song.getArtist() != null && song.getAlbum() != null)
            return;
        try {
            Element body = get163Connection(SONG_URL)
                    .data("id", song.getId())
                    .get().body();

            Element info = body.selectFirst("div[class=cnt]");
            if (info == null)
                throw new ElementNotFoundException("Unable to get song : " + song);
            Elements eleInfo = info.select("a[class=s-fc7]");
            if (eleInfo.size() < 2)
                return;
            Element eleArtist = eleInfo.get(0);
            Artist artist = new Artist(eleArtist.text(), eleArtist.attr("href").substring(11));
            Element eleAlbum = eleInfo.get(1);
            Album album = new Album(artist, eleAlbum.text(), eleAlbum.attr("href").substring(10));
            song.setArtist(artist);
            song.setAlbum(album);
        } catch (IOException e) {
            System.err.printf("Cannot get Artist and Album from song, id: %s\n", song.getId());
        }
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

    public static List<Song> getSongListFromMusicFM(String url) throws IOException {
        Element body = Jsoup.connect(url)
                .get()
                .body();
        Elements songEleList = body.selectFirst("div[class=list]").select("a[class=jstracker]");
        List<Song> songList = new ArrayList<>();
        for (Element songEle : songEleList) {
            String title = songEle.selectFirst("h3").text();
            String artist = songEle.selectFirst("p").text();
            if (title.contains(artist))
                title = title.substring(artist.length() + 3);
            Song song = searchSong(title + " " + artist, 0).get(0);
            songList.add(song);
        }
        return songList;
    }

}