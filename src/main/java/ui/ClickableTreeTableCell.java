package ui;

import com.jfoenix.controls.JFXButton;
import entity.DownloadableEntity;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.MouseButton;

import java.util.function.Function;

public class ClickableTreeTableCell extends TreeTableCell<DownloadableEntity, String> {

    private final Function<DownloadableEntity, String> getParamFunction;
    private final StringParamEvent event;
    private boolean isButton = false;
    private String customName = "";
    private double widthRatio;

    public ClickableTreeTableCell(Function<DownloadableEntity, String> getParamFunction, StringParamEvent event) {
        this.getParamFunction = getParamFunction;
        this.event = event;
        this.parentProperty().addListener(l -> this.getTableColumn().setPrefWidth((Main.WIDTH_PROPERTY.get() - 50.0) * widthRatio));
        // Default Width Ration
        width(0.2);
    }


    @Override
    protected void updateItem(String name, boolean empty) {
        if (!empty) {
            JFXButton button = new JFXButton(customName.isEmpty() ? name : customName);
            button.setStyle(button.getStyle() + "-fx-cursor: hand;");
            if (isButton) {
                button.setStyle(button.getStyle() + "-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
                button.setButtonType(JFXButton.ButtonType.RAISED);
            }
            if (isButton)
                button.setOnMouseClicked(e -> event.run(getParam()));
            else
                button.setOnMouseClicked(e -> {
                    if (e.getButton().equals(MouseButton.PRIMARY))
                        if (e.getClickCount() == 2)
                            event.run(getParam());
                        else if (e.getClickCount() == 1)
                            Center.toast("Double click to do a search!", Center.TOAST_SHORT);
                });
            setGraphic(button);
            setText("");
        } else {
            setGraphic(null);
        }
    }

    private String getParam() {
        return getParamFunction.apply(getEntity());
    }

    public DownloadableEntity getEntity() {
        return this.getTableColumn().getTreeTableView().getTreeItem(this.getIndex()).getValue();
    }

    public boolean isButton() {
        return isButton;
    }

    public ClickableTreeTableCell setIsButton(boolean button) {
        isButton = button;
        return this;
    }

    public String getCustomName() {
        return customName;
    }

    public double getWidthRatio() {
        return widthRatio;
    }

    public ClickableTreeTableCell width(double widthRatio) {
        this.widthRatio = widthRatio;
        Main.WIDTH_PROPERTY.addListener(listener -> {
            this.getTableColumn().setPrefWidth((Main.WIDTH_PROPERTY.get() - 50.0) * widthRatio);
        });
        return this;
    }

    public ClickableTreeTableCell setCustomName(String customName) {
        this.customName = customName;
        return this;
    }

}
