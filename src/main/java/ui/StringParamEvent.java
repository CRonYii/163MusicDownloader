package ui;

import entity.Album;
import entity.Artist;
import entity.Playlist;
import entity.Song;
import util.Database;
import util.Spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface StringParamEvent {

    boolean run(String param);

    class PlaylistDownloadEvent implements StringParamEvent {

        @Override
        public boolean run(String id) {
            try {
                Playlist playlist = Database.getPlaylist(id);
                playlist.downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class SongDownloadEvent implements StringParamEvent {
        @Override
        public boolean run(String id) {
            try {
                Center.toast("Start Downloading Song " + Database.getSong(id).getName());
                Database.getSong(id).download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, id: %s\n", id));
                System.err.printf("Unable to download song, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class AlbumDownloadEvent implements StringParamEvent {
        @Override
        public boolean run(String id) {
            try {
                Database.getAlbum(id).downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class ArtistDownloadEvent implements StringParamEvent {
        @Override
        public boolean run(String id) {
            try {
                Database.getArtist(id).downloadAllSongs();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class FetchMusicFMEvent implements StringParamEvent {
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

    class IdPlaylistSearchEvent implements StringParamEvent {

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
            }
            return true;
        }
    }

    class IdSongSearchEvent implements StringParamEvent {
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
            }
            return true;
        }
    }

    class IdAlbumSearchEvent implements StringParamEvent {
        @Override
        public boolean run(String id) {
            try {
                Center.toast("Searching Album id: " + id);
                Center.setSearchList(Database.getAlbum(id).getSongList());
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class IdArtistSearchEvent implements StringParamEvent {
        @Override
        public boolean run(String id) {
            try {
                Center.toast("Searching Artist id: " + id);
                System.out.println(Database.getArtist(id).getAlbumList().get(0).getSongList());
                Center.setSearchList(Database.getArtist(id).getSongList());
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class KeywordSongSearchEvent implements StringParamEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Center.toast("Searching Song: " + keyword);
                List<Song> songList = Spider.searchSong(keyword, 0);
                Center.setSearchList(songList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class KeywordPlaylistSearchEvent implements StringParamEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Center.toast("Searching Playlist: " + keyword);
                List<Playlist> playList = Spider.searchPlaylist(keyword, 0);
                System.out.println(playList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class KeywordArtistSearchEvent implements StringParamEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Center.toast("Searching Artist: " + keyword);
                List<Artist> artistList = Spider.searchArtist(keyword, 0);
                System.out.println(artistList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    class KeywordAlbumSearchEvent implements StringParamEvent {
        @Override
        public boolean run(String keyword) {
            try {
                Center.toast("Searching Album: " + keyword);
                List<Album> albumList = Spider.searchAlbum(keyword, 0);
                System.out.println(albumList);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}

