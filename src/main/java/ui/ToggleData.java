package ui;

public class ToggleData {

    private final StringParamEvent event;
    private final String data;

    public ToggleData(StringParamEvent event, String data) {
        this.event = event;
        this.data = data;
    }

    public StringParamEvent getEvent() {
        return event;
    }

    public String getData() {
        return data;
    }
}
