package ui;

import entity.Album;
import entity.Playlist;
import entity.Song;
import util.Database;
import util.ElementNotFoundException;
import util.Spider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public interface RunnableEvent {

    boolean run(String id);

    class PlaylistDownloadEvent implements RunnableEvent {

        @Override
        public boolean run(String id) {
            try {
                Playlist playlist = Database.getPlaylist(id);
                playlist.downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class SongDownloadEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Database.getSong(id).download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, id: %s\n", id));
                System.err.printf("Unable to download song, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get song, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class AlbumDownloadEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Database.getAlbum(id).downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get album, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class ArtistDownloadEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Database.getArtist(id).downloadAllAlbum();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class PlaylistSearchEvent implements RunnableEvent {

        @Override
        public boolean run(String id) {
            try {
                Playlist playlist = Database.getPlaylist(id);
                Center.setSearchList(playlist.getSongList());
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class SongSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Song song = Database.getSong(id);
                Set<Song> songList = new HashSet<>();
                songList.add(song);
                Center.setSearchList(songList);
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, id: %s\n", id));
                System.err.printf("Unable to download song, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get song, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class AlbumSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Center.setSearchList(Database.getAlbum(id).getSongList());
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get album, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class ArtistSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String id) {
            try {
                Set<Album> albumList = Database.getArtist(id).getAlbumList();
                Set<Song> songList = new HashSet<>();
                for (Album a : albumList)
                    songList.addAll(a.getSongList());
                Center.setSearchList(songList);
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
                return false;
            } catch (ElementNotFoundException e) {
                Center.printToStatus(String.format("Unable to get artist's songs, id: %s\n", id));
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class KeywordSearchEvent implements RunnableEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Set<Song> songList = Spider.search(keyword, Spider.TYPE_SONG);
                Center.setSearchList(songList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}

