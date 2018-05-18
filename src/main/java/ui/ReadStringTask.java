package ui;

import javafx.concurrent.Task;

class ReadStringTask extends Task<Void> {

    private final String str;
    private final ReadStringEvent event;

    public ReadStringTask(String str, ReadStringEvent event) {
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
