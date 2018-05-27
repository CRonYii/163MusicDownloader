package ui;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import entity.DownloadableEntity;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ui.fxml.TabViewController;
import util.Database;
import util.Downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Center {


    public static final String PLAYLIST = "playlist";
    public static final String SONG = "song";
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final String ID = "id";
    public static final String KEYWORD = "keyword";

    public static final long TOAST_SHORT = 1000;
    public static final long TOAST_REGULAR = 2000;
    public static final long TOAST_LONG = 4000;

    private static File newSongDir;
    private static final List<Runnable> closeEventList = new ArrayList<>();

    public static final EventHandler<WindowEvent> CLOSE_EVENT = (EventHandler<WindowEvent>) event -> {
        // set to new directory
        if (newSongDir != null) {
            for (File file : Database.database.getSongDir().listFiles()) {
                try {
                    if (file.getName().contains(".mp3"))
                        Files.move(file.toPath(), Paths.get(newSongDir.getAbsolutePath() + "\\" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Database.database.setSongDir(newSongDir);
        }
        // delete temp files
        for (File f : Downloader.TEMP_DIR.listFiles()) {
            f.delete();
        }
        // Save data
        closeEventList.add(() -> {
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Database.OUTPUT));
                out.writeObject(Database.database);
            } catch (IOException e) {
                Database.OUTPUT.delete();
                e.printStackTrace();
            }
        });
        for (Runnable runnable : closeEventList) {
            runnable.run();
        }
    };

    private static Label statusLabel;

    private static JFXSnackbar toast;
    private static Scene rootScene;
    private static JFXTreeTableView<DownloadableEntity> searchView;
    private static Label searchListLabel;

    public static void setLabel(Label statusLabel) {
        Center.statusLabel = statusLabel;
    }

    public static JFXTextField setUpIdValidationTextField(String tag) {
        JFXTextField textField = new JFXTextField();
        textField.setValidators(new PositiveNumberValidator("id must be a number"));
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // use regex to fetch song id from url if necessary
            String id = textField.getText().trim();
            if (!id.matches("^[0-9]*$")) {
                String regex = tag + "\\?id=([0-9]*)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(id);
                if (matcher.find())
                    id = matcher.group(1);
            }
            textField.setText(id);

            textField.validate();
        });
        textField.setPromptText("Enter " + tag.substring(0, 1).toUpperCase() + tag.substring(1) + " ID");
        textField.setLabelFloat(true);
        return textField;
    }

    public static JFXTextField setUpTextField(String tag) {
        JFXTextField textField = new JFXTextField();
        textField.setValidators(new NonNullValidator("Keyword cannot be empty"));
        textField.setPromptText("Enter " + tag.substring(0, 1).toUpperCase() + tag.substring(1) + "Name");
        textField.setLabelFloat(true);
        return textField;
    }

    public static void printToStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    public static Window getRootWindow() {
        return rootScene.getWindow();
    }


    public static void setRootScene(Scene rootScene) {
        Center.rootScene = rootScene;
    }

    public static void setSearchList(ObservableList<DownloadableEntity> searchList) {
        TabViewController.instance.setSearchList(searchList);
    }

    public static void addCloseEvent(Runnable runnable) {
        closeEventList.add(runnable);
    }

    public static void setNewSongDir(File newSongDir) {
        Center.newSongDir = newSongDir;
    }


    public static void setToast(JFXSnackbar toast) {
        Center.toast = toast;
    }

    public static void toast(String msg) {
        toast(msg, TOAST_REGULAR);
    }

    public static void toast(String msg, long timeout) {
        Platform.runLater(() -> toast.show(msg, timeout));
    }
}
