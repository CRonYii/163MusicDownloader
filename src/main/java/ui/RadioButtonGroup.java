package ui;

import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.InvalidationListener;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class RadioButtonGroup extends HBox {

    private final ToggleGroup toggleGroup = new ToggleGroup();

    public RadioButtonGroup() {
        super();
    }

    public RadioButtonGroup(double spacing) {
        super(spacing);
    }

    public RadioButtonGroup(double spacing, RadioButton... children) {
        super(spacing, children);
    }

    public RadioButtonGroup(RadioButton... buttons) {
        super(buttons);
    }

    public RadioButtonGroup(String... buttons) {
        super();
        addAll(buttons);
    }

    ;

    public RadioButtonGroup(double spacing, String... buttons) {
        super(spacing);
        addAll(buttons);
    }

    ;

    public void selectRadioButton(int i) {
        toggleGroup.selectToggle((Toggle) getChildren().get(i));
    }

    public void add(int index, RadioButton radioBtn) {
        radioBtn.setToggleGroup(toggleGroup);
        this.getChildren().add(index, radioBtn);
    }

    public void add(RadioButton radioBtn) {
        add(lastIndex(), radioBtn);
    }

    public void add(String text) {
        JFXRadioButton radioBtn = new JFXRadioButton(text);
        add(lastIndex(), radioBtn);
    }

    public void add(int index, String text) {
        JFXRadioButton radioBtn = new JFXRadioButton(text);
        add(index, radioBtn);
    }

    public void addAll(RadioButton... buttons) {
        for (RadioButton button : buttons) {
            add(button);
        }
    }

    public void addAll(String... buttons) {
        for (String button : buttons) {
            add(button);
        }
    }

    public void onSelect(InvalidationListener event) {
        toggleGroup.selectedToggleProperty().addListener(event);
    }

    public String getSelectedData() {
        return toggleGroup.getSelectedToggle().getUserData().toString();
    }

    public Toggle getSelected() {
        return toggleGroup.getSelectedToggle();
    }

    private int lastIndex() {
        return this.getChildren().size();
    }

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }
}
