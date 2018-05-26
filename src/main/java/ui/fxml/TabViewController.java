package ui.fxml;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import entity.Entity;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.*;
import util.Downloader;
import util.ThreadUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static ui.Center.*;

// TODO: 1. Make Entity Type Generic 2. Put the search View into separate class 3. Support All Entity Type display in searchView 4. implement Paging 5. Possibly Forward / Backward Navigation
public class TabViewController {

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
    private JFXTreeTableView<Entity> searchView;
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

    public static TabViewController instance;

    @FXML
    @PostConstruct
    public void initialize() {
        instance = this;

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

        searchFilterField.textProperty().addListener((observable, oldValue, newValue) ->
                searchView.setPredicate(prop -> {
                    String filter = newValue.toLowerCase();
                    Map<String, StringProperty> propertyMap = prop.getValue().getPropertyMap();
                    for (StringProperty property : propertyMap.values())
                        if (property.get().toLowerCase().contains(filter))
                            return true;
                    return false;
                }));

        selectionLabel.textProperty().bind(Bindings.createStringBinding(
                () -> searchView.getSelectionModel().getSelectedCells().size() + " result(s) selected",
                searchView.getSelectionModel().getSelectedItems())
        );
    }

    public void setSearchList(List<Entity> searchList) {
        if (searchList.isEmpty()) {
            Center.toast(String.format("Found 0 results on Search"), TOAST_LONG);
            return;
        }
        Platform.runLater(() -> {
            ObservableList<Entity> dataList = FXCollections.observableArrayList(searchList);
            searchListLabel.setText(String.format("Found %s results", searchList.size()));

            searchView.getColumns().clear();
            Entity entity = searchList.get(0);
            List<String> columns = entity.getColumns();
            double width = Main.WIDTH / columns.size();
            columns.forEach(columnName -> {
                JFXTreeTableColumn<Entity, String> column = new JFXTreeTableColumn<>(columnName);
                column.setPrefWidth(width);
                setUpCellValueFactory(column, (Entity e) -> e.getPropertyMap().get(columnName));
                if (entity.getColumnFactoryMap().containsKey(columnName) && entity.getColumnFactoryMap().get(columnName) != null)
                    column.setCellFactory(entity.getColumnFactoryMap().get(columnName));
                searchView.getColumns().add(column);
            });
            searchView.setRoot(new RecursiveTreeItem<>(dataList, RecursiveTreeObject::getChildren));
        });
    }

    private void setUpCellValueFactory(JFXTreeTableColumn<Entity, String> column, Function<Entity, StringProperty> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Entity, String> param) -> {
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
        if (searchTextField != null)
            field.setText(searchTextField.getText());
        field.validate();
        searchBox.getChildren().remove(searchTextField);
        searchTextField = field;
        searchBox.getChildren().add(0, searchTextField);
    }

    // TODO: Might need Entity Support Download
    @FXML
    public void downloadAll() {
        if (searchView.getRoot() == null)
            return;
        for (TreeItem<Entity> songTreeItem : searchView.getRoot().getChildren()) {
//            songTreeItem.getValue().download();
        }
    }

    @FXML
    public void downloadSelected() {
        for (TreeTablePosition<Entity, ?> cell : searchView.getSelectionModel().getSelectedCells()) {
//            cell.getTreeItem().getValue().download();
        }
    }
}
