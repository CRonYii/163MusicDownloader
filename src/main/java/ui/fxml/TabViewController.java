package ui.fxml;

import com.jfoenix.controls.*;
import entity.Song;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import ui.*;
import util.Downloader;
import util.ThreadUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class TabViewController implements Initializable {

    private final ToggleGroup selectToggle = new ToggleGroup();
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
    private HBox selectType;
    @FXML
    private JFXTextField searchTextField;
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

        listView.setItems(Downloader.getInstance().getDownloadList());
        listView.setCellFactory(cell -> new DownloadCell());

        downloadTab.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Download (%s)", Downloader.getInstance().getDownloadList().size()),
                Downloader.getInstance().getDownloadList())
        );

        downloadSelectedButton.disableProperty().bind(Bindings.createBooleanBinding(() -> searchView.getSelectionModel().getSelectedItems().size() == 0, searchView.getSelectionModel().getSelectedItems()));
    }


    private void setUpRdToggle() {
        setUpRadioButton(new StringParamEvent.IdPlaylistSearchEvent(), "playlist");
        setUpRadioButton(new StringParamEvent.IdArtistSearchEvent(), "artist");
        setUpRadioButton(new StringParamEvent.IdAlbumSearchEvent(), "album");
        setUpRadioButton(new StringParamEvent.IdSongSearchEvent(), "song");

        selectToggle.selectedToggleProperty().addListener(
                event -> Center.setUpIdValidationTextField(
                        ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getData(), searchTextField));

        selectToggle.selectToggle(selectToggle.getToggles().get(0));
    }

    private void setUpRadioButton(StringParamEvent event, String data) {
        JFXRadioButton radioButton = new JFXRadioButton(data.substring(0, 1).toUpperCase() + data.substring(1));
        radioButton.setPadding(new Insets(10));
        radioButton.setToggleGroup(selectToggle);
        radioButton.setUserData(new ToggleData(event, data));
        selectType.getChildren().add(radioButton);
    }

    @FXML
    public void search() {
        if (selectToggle.getSelectedToggle() != null && searchTextField.validate()) {
            // Start a new Thread to search in background
            String id = searchTextField.getText();
            searchTextField.clear();
            StringParamEvent event = ((ToggleData) selectToggle.getSelectedToggle().getUserData()).getEvent();
            ReadStringTask searchTask = new ReadStringTask(id, event);
            searchProgress.visibleProperty().bind(searchTask.runningProperty());
            ThreadUtils.startNormalThread(searchTask);
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
                    button.setOnAction(event -> ThreadUtils.startNormalThread(new ReadStringTask(id, new StringParamEvent.SongDownloadEvent())));
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
                    return song.getTitle().toLowerCase().contains(filter)
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