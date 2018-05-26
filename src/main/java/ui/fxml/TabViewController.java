package ui.fxml;

import com.jfoenix.controls.*;
import entity.Song;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.*;
import util.Downloader;
import util.ThreadUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import static ui.Center.*;

public class TabViewController implements Initializable {

    private RadioButtonGroup searchTypeButtonGroup;

    private RadioButtonGroup resultTypeButtonGroup;

    private JFXTextField searchTextField;

    @FXML
    private VBox searchVBox;
    @FXML
    private HBox searchBox;
    @FXML
    private JFXListView<Downloader.Download> listView;
    @FXML
    private JFXTreeTableView<Song> searchView;
    @FXML
    private JFXTreeTableColumn<Song, String> titleColumn;
    @FXML
    private JFXTreeTableColumn<Song, String> artistColumn;
    @FXML
    private JFXTreeTableColumn<Song, String> albumColumn;
    @FXML
    private JFXTreeTableColumn<Song, String> actionColumn;
    @FXML
    private JFXProgressBar searchProgress;
    @FXML
    private JFXTextField searchFilterField;
    @FXML
    private Label searchListLabel;
    @FXML
    private Label selectionLabel;
    @FXML
    private Tab downloadTab;
    @FXML
    private JFXButton downloadSelectedButton;
    @FXML
    private JFXButton downloadAllButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpRdToggle();
        initSearchView();

        listView.setItems(Downloader.downloader.getDownloadList());
        listView.setCellFactory(cell -> new DownloadCell());

        downloadTab.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Download (%s)", Downloader.downloader.getDownloadList().size()),
                Downloader.downloader.getDownloadList())
        );

        downloadSelectedButton.disableProperty().bind(Bindings.createBooleanBinding(() -> searchView.getSelectionModel().getSelectedItems().size() == 0, searchView.getSelectionModel().getSelectedItems()));
    }

    private void setUpRdToggle() {
        searchTypeButtonGroup = new RadioButtonGroup(10.0);
        searchTypeButtonGroup.setPadding(new Insets(10.0));
        setUpRadioButton(KEYWORD, searchTypeButtonGroup);
        setUpRadioButton(ID, searchTypeButtonGroup);

        resultTypeButtonGroup = new RadioButtonGroup(10.0);
        resultTypeButtonGroup.setPadding(new Insets(10.0));
        setUpRadioButton(SONG, resultTypeButtonGroup);
        setUpRadioButton(ALBUM, resultTypeButtonGroup);
        setUpRadioButton(ARTIST, resultTypeButtonGroup);
        setUpRadioButton(PLAYLIST, resultTypeButtonGroup);

        searchTypeButtonGroup.selectRadioButton(0);
        resultTypeButtonGroup.selectRadioButton(0);

        searchTypeButtonGroup.onSelect(event -> updateTextField());
        searchTypeButtonGroup.setAlignment(Pos.CENTER);
        searchVBox.getChildren().add(1, searchTypeButtonGroup);

        resultTypeButtonGroup.onSelect(event -> updateTextField());
        resultTypeButtonGroup.setAlignment(Pos.CENTER);
        searchVBox.getChildren().add(2, resultTypeButtonGroup);

        updateTextField();
        Platform.runLater(() -> searchTextField.requestFocus());
    }

    private void setUpRadioButton(String data, RadioButtonGroup group) {
        JFXRadioButton radioButton = new JFXRadioButton(data.substring(0, 1).toUpperCase() + data.substring(1));
        radioButton.setUserData(data);
        group.add(radioButton);
    }

    @FXML
    private void search() {
        if (resultTypeButtonGroup.getSelected() != null && searchTextField.validate()) {
            // Start a new Thread to search in background
            String id = searchTextField.getText();
            ReadStringTask searchTask = getSearchTask();
            searchProgress.visibleProperty().bind(searchTask.runningProperty());
            ThreadUtils.startThread(searchTask);
        }
    }

    private void initSearchView() {
        searchView.setEditable(false);
        searchView.setShowRoot(false);
        searchView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupCellValueFactory(titleColumn, Song::titlePropertyProperty);
        setupCellValueFactory(artistColumn, Song::artistNameProperty);
        setupCellValueFactory(albumColumn, Song::albumNameProperty);
        setupCellValueFactory(actionColumn, Song::IDPropertyProperty);
        actionColumn.setCellFactory(param -> new TreeTableCell<Song, String>() {
            @Override
            protected void updateItem(String id, boolean empty) {
                if (!empty) {
                    JFXButton button = new JFXButton("Download");
                    button.setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
                    button.setButtonType(JFXButton.ButtonType.RAISED);
                    button.setOnAction(event -> ThreadUtils.startThread(new ReadStringTask(id, new StringParamEvent.SongDownloadEvent())));
                    setGraphic(button);
                    setText("");
                } else {
                    setGraphic(null);
                }
            }
        });

        searchFilterField.textProperty().addListener((observable, oldValue, newValue) ->
                searchView.setPredicate(songProp -> {
                    final Song song = songProp.getValue();
                    String filter = newValue.toLowerCase();
                    return song.getName().toLowerCase().contains(filter)
                            || (song.getArtist() != null && song.getArtist().getName().toLowerCase().contains(filter))
                            || (song.getAlbum() != null && song.getAlbum().getName().toLowerCase().contains(filter));
                }));
        selectionLabel.textProperty().bind(Bindings.createStringBinding(
                () -> searchView.getSelectionModel().getSelectedCells().size() + " song(s) selected",
                searchView.getSelectionModel().getSelectedItems()));

        Center.setSearchView(searchView);
        Center.setSearchListLabel(searchListLabel);
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<Song, T> column, Function<Song, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Song, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private ReadStringTask getSearchTask() {
        switch (searchTypeButtonGroup.getSelectedData()) {
            case KEYWORD:
                return new ReadStringTask(searchTextField.getText(), getKeywordSearchEvent());
            case ID:
                return new ReadStringTask(searchTextField.getText(), getIDSearchEvent());
        }
        return null;
    }

    private StringParamEvent getKeywordSearchEvent() {
        switch (resultTypeButtonGroup.getSelectedData()) {
            case PLAYLIST:
                return new StringParamEvent.KeywordPlaylistSearchEvent();
            case SONG:
                return new StringParamEvent.KeywordSongSearchEvent();
            case ALBUM:
                return new StringParamEvent.KeywordAlbumSearchEvent();
            case ARTIST:
                return new StringParamEvent.KeywordArtistSearchEvent();
        }
        return null;
    }

    private StringParamEvent getIDSearchEvent() {
        switch (resultTypeButtonGroup.getSelectedData()) {
            case PLAYLIST:
                return new StringParamEvent.IdPlaylistSearchEvent();
            case SONG:
                return new StringParamEvent.IdSongSearchEvent();
            case ALBUM:
                return new StringParamEvent.IdAlbumSearchEvent();
            case ARTIST:
                return new StringParamEvent.IdArtistSearchEvent();
        }
        return null;
    }

    private void updateTextField() {
        if (searchTypeButtonGroup.getSelectedData().equals(ID))
            updateTextField(Center.setUpIdValidationTextField(resultTypeButtonGroup.getSelectedData()));
        else if (searchTypeButtonGroup.getSelectedData().equals(KEYWORD))
            updateTextField(Center.setUpTextField(resultTypeButtonGroup.getSelectedData()));
    }

    private void updateTextField(JFXTextField field) {
        field.setOnAction(event -> search());
        searchBox.getChildren().remove(searchTextField);
        searchTextField = field;
        searchBox.getChildren().add(0, searchTextField);
    }

    @FXML
    public void downloadAll() {
        if (searchView.getRoot() == null)
            return;
        for (TreeItem<Song> songTreeItem : searchView.getRoot().getChildren()) {
            songTreeItem.getValue().download();
        }
    }

    @FXML
    public void downloadSelected() {
        for (TreeTablePosition<Song, ?> cell : searchView.getSelectionModel().getSelectedCells()) {
            cell.getTreeItem().getValue().download();
        }
    }
}
