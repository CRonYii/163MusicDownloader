package ui;

import entity.Playlist;
import util.Database;

import java.io.IOException;

public interface DownloadEvent {

    boolean run(String param);

    class PlaylistDownloadEvent implements DownloadEvent {

        @Override
        public boolean run(String id) {
            try {
                Playlist playlist = Database.getPlaylist(id);
                playlist.download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to get playlist, id: %s\n", id));
                System.err.printf("Unable to get playlist, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class SongDownloadEvent implements DownloadEvent {
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

    class AlbumDownloadEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Database.getAlbum(id).download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download album, id: %s\n", id));
                System.err.printf("Unable to download album, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

    class ArtistDownloadEvent implements DownloadEvent {
        @Override
        public boolean run(String id) {
            try {
                Database.getArtist(id).download();
            } catch (IOException e) {
                Center.printToStatus(String.format("Unable to download artist's songs, id: %s\n", id));
                System.err.printf("Unable to download artist's songs, id: %s\n", id);
                return false;
            }
            return true;
        }
    }

}

