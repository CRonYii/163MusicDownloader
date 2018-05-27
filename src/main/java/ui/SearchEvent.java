package ui;

import entity.*;
import util.Database;
import util.Spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface SearchEvent {

    List<DownloadableEntity> run(String input, int offset);

    class KeywordSongSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String keyword, int offset) {
            try {
                Center.toast("Searching Song: " + keyword);
                List<Song> songList = Spider.searchSong(keyword, offset);
                return new ArrayList<>(songList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class KeywordPlaylistSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String keyword, int offset) {
            try {
                Center.toast("Searching Playlist: " + keyword);
                List<Playlist> playList = Spider.searchPlaylist(keyword, offset);
                return new ArrayList<>(playList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class KeywordArtistSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String keyword, int offset) {
            try {
                Center.toast("Searching Artist: " + keyword);
                List<Artist> artistList = Spider.searchArtist(keyword, offset);
                return new ArrayList<>(artistList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class KeywordAlbumSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String keyword, int offset) {
            try {
                Center.toast("Searching Album: " + keyword);
                List<Album> albumList = Spider.searchAlbum(keyword, offset);
                return new ArrayList<>(albumList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class SingleSearchEvent {
    }

    class IdPlaylistSearchEvent extends SingleSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String id, int offset) {
            try {
                Center.toast("Searching Playlist id: " + id);
                Playlist playlist = Database.getPlaylist(id);
                return new ArrayList<>(playlist.getSongList());
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
            }
            return null;
        }
    }

    class IdSongSearchEvent extends SingleSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String id, int offset) {
            try {
                Center.toast("Searching Song id: " + id);
                Song song = Database.getSong(id);
                List<Song> songList = new ArrayList<>();
                songList.add(song);
                return new ArrayList<>(songList);
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download song, id: %s\n", id));
                System.err.printf("Unable to download song, id: %s\n", id);
            }
            return null;
        }
    }

    class IdAlbumSearchEvent extends SingleSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String id, int offset) {
            try {
                Center.toast("Searching Album id: " + id);
                List<Song> songList = Database.getAlbum(id).getSongList();
                return new ArrayList<>(songList);
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
            }
            return null;
        }
    }

    class IdArtistSearchEvent extends SingleSearchEvent implements SearchEvent {
        @Override
        public List<DownloadableEntity> run(String id, int offset) {
            try {
                Center.toast("Searching Artist id: " + id);
                List<Song> songList = Database.getArtist(id).getSongList();
                return new ArrayList<>(songList);
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
            }
            return null;
        }
    }

}
