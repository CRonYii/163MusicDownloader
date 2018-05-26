package ui;

import com.jfoenix.controls.JFXButton;
import entity.DownloadableEntity;
import javafx.scene.control.TreeTableCell;
import util.ThreadUtils;

import java.util.function.Function;

public class ClickableTreeTableCell extends TreeTableCell<DownloadableEntity, String> {

    private final Function<DownloadableEntity, String> getIdFunction;
    private final StringParamEvent event;
    private boolean isButton = false;
    private String customName = "";

    public ClickableTreeTableCell(Function<DownloadableEntity, String> getIdFunction, StringParamEvent event) {
        this.getIdFunction = getIdFunction;
        this.event = event;
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
            button.setOnAction(e -> ThreadUtils.startThread(new ReadStringTask(getIdFunction.apply(getEntity()), event)));
            setGraphic(button);
            setText("");
        } else {
            setGraphic(null);
        }
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

    public ClickableTreeTableCell setCustomName(String customName) {
        this.customName = customName;
        return this;
    }

}
