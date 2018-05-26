package ui.fxml;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.Center;
import ui.ReadStringTask;
import ui.StringParamEvent;
import util.Database;
import util.ThreadUtils;

import java.awt.*;
import java.io.IOException;

public class DownloadPopupController {

    @FXML
    public void downloadPlaylist() {
        promptEnterIDDialog(new StringParamEvent.PlaylistDownloadEvent(), "playlist", "All songs in the playlist will be downloaded");
    }

    @FXML
    public void downloadSong() {
        promptEnterIDDialog(new StringParamEvent.SongDownloadEvent(), "song", "The Song will be downloaded");
    }

    @FXML
    public void downloadAlbum() {
        promptEnterIDDialog(new StringParamEvent.AlbumDownloadEvent(), "album", "All songs in the album will be downloaded");
    }

    @FXML
    public void downloadArtist() {
        promptEnterIDDialog(new StringParamEvent.ArtistDownloadEvent(), "artist", "All songs of artist will be downloaded");
    }

    @FXML
    public void openSongsFolder() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(Database.database.getSongDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void promptEnterIDDialog(StringParamEvent task, String tag, String promptMsg) {
        JFXAlert alert = new JFXAlert((Stage) Center.getRootWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new javafx.scene.control.Label("Enter an id number"));

        Label promptLabel = new Label(promptMsg);
        JFXTextField textField = Center.setUpIdValidationTextField(tag);
        VBox body = new VBox(promptLabel, textField);
        body.setSpacing(20.0);

        layout.setBody(body);

        JFXButton acceptButton = new JFXButton("ACCEPT");
        acceptButton.getStyleClass().add("dialog-accept");
        acceptButton.setOnAction(event -> {
            if (textField.validate()) {
                // Start a new Thread to download the song in background
                String id = textField.getText();
                alert.hideWithAnimation();

                ThreadUtils.startThread(new ReadStringTask(id, task));
            }
        });

        JFXButton closeButton = new JFXButton("CANCEL");
        closeButton.setOnAction(event -> alert.hideWithAnimation());

        layout.setActions(acceptButton, closeButton);

        alert.setContent(layout);
        alert.show();
    }

    private void promptEnterURLDialog(StringParamEvent task, String promptMsg) {
        JFXAlert alert = new JFXAlert((Stage) Center.getRootWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new javafx.scene.control.Label("Enter a URL"));

        Label promptLabel = new Label(promptMsg);
        JFXTextField textField = new JFXTextField();
        textField.setPromptText("URL");
        textField.setLabelFloat(true);
        VBox body = new VBox(promptLabel, textField);
        body.setSpacing(20.0);

        layout.setBody(body);

        JFXButton acceptButton = new JFXButton("ACCEPT");
        acceptButton.getStyleClass().add("dialog-accept");
        acceptButton.setOnAction(event -> {
            String url = textField.getText();
            alert.hideWithAnimation();

            ThreadUtils.startThread(new ReadStringTask(url, task));
        });

        JFXButton closeButton = new JFXButton("CANCEL");
        closeButton.setOnAction(event -> alert.hideWithAnimation());

        layout.setActions(acceptButton, closeButton);

        alert.setContent(layout);
        alert.show();
    }

}
