package ui;

import javafx.concurrent.Task;

public class ReadStringTask extends Task<Void> {

    private final String str;
    private final DownloadEvent event;

    public ReadStringTask(String str, DownloadEvent event) {
        this.str = str;
        this.event = event;
    }

    @Override
    protected Void call() {
        if (this.event.run(str)) {
            succeeded();
        } else {
            failed();
        }
        return null;
    }

    @Override
    protected void failed() {
        super.failed();
        exceptionProperty().get().printStackTrace();
    }
}
