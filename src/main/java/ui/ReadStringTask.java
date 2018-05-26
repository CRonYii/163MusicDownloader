package ui;

import javafx.concurrent.Task;

public class ReadStringTask<Void> extends Task<Void> {

    private final String str;
    private final StringParamEvent event;

    public ReadStringTask(String str, StringParamEvent event) {
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
}
