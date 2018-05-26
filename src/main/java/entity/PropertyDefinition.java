package entity;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class PropertyDefinition {

    private final String name;
    private final String getter;
    private final String setter;
    private Callback<TreeTableColumn<DownloadableEntity, String>, TreeTableCell<DownloadableEntity, String>> cell;

    public PropertyDefinition(String name, String getter, String setter, Callback<TreeTableColumn<DownloadableEntity, String>, TreeTableCell<DownloadableEntity, String>> cell) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.cell = cell;
    }

    public PropertyDefinition(String name, String getter, String setter) {
        this(name, getter, setter, null);
    }

    public PropertyDefinition(String name) {
        this(name, getGetterName(name), getSetterName(name));
    }

    public static String getGetterName(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
    }

    public static String getSetterName(String name) {
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
    }

    public String getName() {
        return name;
    }

    public String getGetter() {
        return getter;
    }

    public String getSetter() {
        return setter;
    }

    public Callback<TreeTableColumn<DownloadableEntity, String>, TreeTableCell<DownloadableEntity, String>> getCell() {
        return cell;
    }

    public PropertyDefinition setCell(Callback<TreeTableColumn<DownloadableEntity, String>, TreeTableCell<DownloadableEntity, String>> cell) {
        this.cell = cell;
        return this;
    }
}