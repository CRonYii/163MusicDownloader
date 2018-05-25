package ui;

import entity.Album;
import entity.Playlist;
import entity.Song;
import util.Database;
import util.ElementNotFoundException;
import util.Spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ReadStringEvent {

    boolean run(String param);

    class PlaylistDownloadEvent implements ReadStringEvent {

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

    class SongDownloadEvent implements ReadStringEvent {
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

    class AlbumDownloadEvent implements ReadStringEvent {
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

    class ArtistDownloadEvent implements ReadStringEvent {
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

    class FetchMusicFMEvent implements ReadStringEvent {
        @Override
        public boolean run(String url) {
            try {
                List<Song> songList = Spider.getSongListFromMusicFM(url);
                Center.setSearchList(songList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class IdPlaylistSearchEvent implements ReadStringEvent {

        @Override
        public boolean run(String id) {
            try {
                Center.toast("Searching Playlist id: " + id);
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

    class IdSongSearchEvent implements ReadStringEvent {
        @Override
        public boolean run(String id) {
            try {
                Center.toast("Searching Song id: " + id);
                Song song = Database.getSong(id);
                List<Song> songList = new ArrayList<>();
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

    class IdAlbumSearchEvent implements ReadStringEvent {
        @Override
        public boolean run(String id) {
            try {
                Center.toast("Searching Album id: " + id);
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

    class IdArtistSearchEvent implements ReadStringEvent {
        @Override
        public boolean run(String id) {
            try {
                Center.toast("Searching Artist id: " + id);
                List<Album> albumList = Database.getArtist(id).getAlbumList();
                List<Song> songList = new ArrayList<>();
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

    class KeywordSongSearchEvent implements ReadStringEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Center.toast("Searching Song keyword: " + keyword);
                List<Song> songList = Spider.searchSong(keyword);
                Center.setSearchList(songList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}

