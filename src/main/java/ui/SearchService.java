package ui;

import entity.DownloadableEntity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import ui.fxml.TabViewController;
import util.ThreadUtils;

import java.util.List;

public class SearchService {

    private static SearchService instance = new SearchService();
    private boolean newCreated = true;
    private boolean finished = false;
    private String keyword;
    private int offset = 0;
    private SearchEvent event;
    private ObservableList<DownloadableEntity> dataList = FXCollections.observableArrayList();
    private Task<Void> searchTask;

    private SearchService() {
    }

    public static SearchService create(String keyword, SearchEvent task) {
        instance.newCreated = true;
        instance.finished = false;
        instance.keyword = keyword;
        instance.event = task;
        instance.dataList = FXCollections.observableArrayList();
        instance.offset = 0;
        return instance;
    }

    public static SearchService instance() {
        return instance;
    }

    public synchronized void load() {
        if (searchTask == null || !searchTask.isRunning()) {
            searchTask = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    if (finished)
                        return null;
                    List<DownloadableEntity> resultList = event.run(keyword, offset++);
                    dataList.addAll(resultList);
                    Platform.runLater(() -> TabViewController.instance.getSearchView().setCurrentItemsCount(TabViewController.instance.getSearchView().getCurrentItemsCount() + resultList.size()));
                    finished = resultList.isEmpty();
                    if (event instanceof SearchEvent.SingleSearchEvent)
                        finished = true;
                    if (newCreated) { // If is new created and is finished, rollback to previous search
                        Center.setSearchList(dataList);
                        newCreated = false;
                    }
                    return null;
                }

                @Override
                protected void failed() {
                    super.failed();
                    exceptionProperty().get().printStackTrace();
                }
            };
            TabViewController.instance.getSearchProgress().visibleProperty().bind(searchTask.runningProperty());
            ThreadUtils.startThread(searchTask);
        }
    }

    public boolean isNewCreated() {
        return newCreated;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getOffset() {
        return offset;
    }

    public SearchEvent getEvent() {
        return event;
    }

    public ObservableList<DownloadableEntity> getDataList() {
        return dataList;
    }
}
