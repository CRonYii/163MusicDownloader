package ui.fxml;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import entity.DownloadableEntity;
import entity.Entity;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import java.util.TimerTask;
import java.util.function.Function;

import static ui.Center.*;

// TODO: Possibly Forward / Backward Navigation
public class TabViewController {

    private RadioButtonGroup searchTypeButtonGroup;

    private RadioButtonGroup resultTypeButtonGroup;

    private JFXTextField searchTextField;

    @FXML
    private VBox searchVBox;
    @FXML
    private HBox searchBox;
    @FXML
    private JFXListView<Downloader.Download> downloadListView;
    @FXML
    private JFXTreeTableView<DownloadableEntity> searchView;
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

    private final long SCROLL_INVOKE_TIME = 1000;
    private final long SCROLL_TO_SEARCH_TIME = 4;
    private long scrollCounter = 0;
    private final TimerTask scrollTask = new TimerTask() {
        @Override
        public void run() {
            scrollCounter = 0;
        }
    };

    @FXML
    @PostConstruct
    public void initialize() {
        instance = this;

        setUpRdToggle();
        initSearchView();

        downloadListView.setItems(Downloader.downloader.getDownloadList());
        downloadListView.setCellFactory(cell -> new DownloadCell());

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
            String input = searchTextField.getText();
            searchTextField.clear();
            switch (searchTypeButtonGroup.getSelectedData()) {
                case KEYWORD:
                    SearchService.create(input, getKeywordSearchEvent()).load();
                    break;
                case ID:
                    SearchService.create(input, getIDSearchEvent()).load();
                    break;
            }
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

        searchListLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Found %s results", searchView.getCurrentItemsCount()),
                searchView.currentItemsCountProperty())
        );

        searchView.setOnScroll(event -> {
            if (SearchService.instance().isFinished())
                return;
            if ((event.getTarget() instanceof TreeTableCell || event.getTarget() instanceof Button) && event.getDeltaY() < 0) {
                if (scrollCounter == 0) {
                    Center.toast("Keep Scrolling to search more", TOAST_SHORT);
                    ThreadUtils.startDelayedThread(scrollTask, SCROLL_INVOKE_TIME);
                }
                if (++scrollCounter > SCROLL_TO_SEARCH_TIME) {
                    scrollCounter = 0;
                    scrollTask.cancel();
                    SearchService.instance().load();
                }
            }
        });
    }

    public void setSearchList(ObservableList<DownloadableEntity> dataList) {
        if (dataList.isEmpty()) {
            Center.toast("Found 0 results on Search", TOAST_LONG);
            return;
        }
        Platform.runLater(() -> {
            searchView.getColumns().clear();
            Entity entity = dataList.get(0);

            List<String> columns = entity.getColumns();
            double width = Main.WIDTH / columns.size() + 1;
            // Add the default id (auto-increment) Column
            JFXTreeTableColumn<DownloadableEntity, String> defaultColumn = new JFXTreeTableColumn<>("#");
            defaultColumn.setPrefWidth(width);
            defaultColumn.setCellValueFactory(param -> new SimpleStringProperty(SearchService.instance().getDataList().indexOf(param.getValue().getValue()) + 1 + ""));
            searchView.getColumns().add(defaultColumn);
            // Add the customize Columns
            columns.forEach(columnName -> {
                JFXTreeTableColumn<DownloadableEntity, String> column = new JFXTreeTableColumn<>(columnName);
                column.setPrefWidth(width);
                setUpCellValueFactory(column, (DownloadableEntity e) -> e.getPropertyMap().get(columnName));
                if (entity.getColumnFactoryMap().containsKey(columnName) && entity.getColumnFactoryMap().get(columnName) != null)
                    column.setCellFactory(entity.getColumnFactoryMap().get(columnName));
                searchView.getColumns().add(column);
            });
            searchView.setRoot(new RecursiveTreeItem<>(dataList, RecursiveTreeObject::getChildren));
            searchView.scrollTo(0);
        });
    }

    private void setUpCellValueFactory(JFXTreeTableColumn<DownloadableEntity, String> column, Function<DownloadableEntity, StringProperty> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<DownloadableEntity, String> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private SearchEvent getKeywordSearchEvent() {
        switch (resultTypeButtonGroup.getSelectedData()) {
            case PLAYLIST:
                return new SearchEvent.KeywordPlaylistSearchEvent();
            case SONG:
                return new SearchEvent.KeywordSongSearchEvent();
            case ALBUM:
                return new SearchEvent.KeywordAlbumSearchEvent();
            case ARTIST:
                return new SearchEvent.KeywordArtistSearchEvent();
        }
        return null;
    }

    private SearchEvent getIDSearchEvent() {
        switch (resultTypeButtonGroup.getSelectedData()) {
            case PLAYLIST:
                return new SearchEvent.IdPlaylistSearchEvent();
            case SONG:
                return new SearchEvent.IdSongSearchEvent();
            case ALBUM:
                return new SearchEvent.IdAlbumSearchEvent();
            case ARTIST:
                return new SearchEvent.IdArtistSearchEvent();
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

    @FXML
    public void downloadAll() {
        if (searchView.getRoot() == null)
            return;
        for (TreeItem<DownloadableEntity> entityTreeItem : searchView.getRoot().getChildren()) {
            entityTreeItem.getValue().download();
        }
    }

    @FXML
    public void downloadSelected() {
        for (TreeTablePosition<DownloadableEntity, ?> cell : searchView.getSelectionModel().getSelectedCells()) {
            cell.getTreeItem().getValue().download();
        }
    }

    public JFXTreeTableView<DownloadableEntity> getSearchView() {
        return searchView;
    }

    public JFXProgressBar getSearchProgress() {
        return searchProgress;
    }
}
