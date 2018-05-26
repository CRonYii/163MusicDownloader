package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Artist implements Serializable {

    private static final long serialVersionUID = 502L;

    private final String name;
    private final String id;
    private final List<Album> albumList;

    public Artist(String name) {
        this(name, null, new ArrayList<>());
    }
    
    public Artist(String name, String id) {
        this(name, id, new ArrayList<>());
    }

    public Artist(String name, String id, List<Album> albumList) {
        this.name = name;
        this.id = id;
        this.albumList = albumList;
    }

    public void addAlbum(Album album) {
        albumList.add(album);
    }

    public void downloadAllAlbum() {
        for (Album a : albumList) {
            a.downloadAllSongs();
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
