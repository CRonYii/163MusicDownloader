package ui;

public class ToggleData {

    private final ReadStringEvent event;
    private final String data;

    public ToggleData(ReadStringEvent event, String data) {
        this.event = event;
        this.data = data;
    }

    public ReadStringEvent getEvent() {
        return event;
    }

    public String getData() {
        return data;
    }
}
