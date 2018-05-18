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

    class PlaylistSearchEvent implements ReadStringEvent {

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

    class SongSearchEvent implements ReadStringEvent {
        @Override
        public boolean run(String id) {
            try {
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

    class AlbumSearchEvent implements ReadStringEvent {
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

    class ArtistSearchEvent implements ReadStringEvent {
        @Override
        public boolean run(String id) {
            try {
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

    class KeywordSearchEvent implements ReadStringEvent {
        @Override
        public boolean run(String keyword) {
            try {
                List<Song> songList = Spider.search(keyword);
                Center.setSearchList(songList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}

