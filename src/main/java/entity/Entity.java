package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Entity extends RecursiveTreeObject<DownloadableEntity> implements Serializable {

    private static final long serialVersionUID = 505L;

    protected static String NULL_GETTER = "nullGetter";
    protected static String NULL_SETTER = "nullSetter";

    protected transient Map<String, Callback<TreeTableColumn<DownloadableEntity, String>, TreeTableCell<DownloadableEntity, String>>> columnFactoryMap = new HashMap<>();
    protected transient Map<String, StringProperty> propertyMap = new HashMap<>();
    private transient JavaBeanStringPropertyBuilder propertyBuilder = JavaBeanStringPropertyBuilder.create().bean(this);

    protected Entity() {
        try {
            bindProperty();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, NoSuchMethodException {
        in.defaultReadObject();
        propertyBuilder = JavaBeanStringPropertyBuilder.create().bean(this);
        propertyMap = new HashMap<>();
        columnFactoryMap = new HashMap<>();
        bindProperty();
    }

    public static PropertyDefinition prop(String name) {
        return new PropertyDefinition(name);
    }

    public static PropertyDefinition prop(String name, String getter, String setter) {
        return new PropertyDefinition(name, getter, setter);
    }

    public static PropertyDefinition constProp(String name) {
        return prop(name, PropertyDefinition.getGetterName(name), NULL_SETTER);
    }

    public static PropertyDefinition constProp(String name, String getter) {
        return prop(name, getter, NULL_SETTER);
    }

    public abstract List<String> getColumns();

    public abstract List<PropertyDefinition> getProperties();

    protected final void bindProperty() throws NoSuchMethodException {
        List<String> columns = getColumns();
        List<PropertyDefinition> properties = getProperties();
        if (properties.size() != columns.size())
            throw new IllegalArgumentException(String.format("Must have %s properties", getColumns().size()));
        for (int i = 0; i < columns.size(); i++) {
            propertyMap.put(columns.get(i), getProperty(properties.get(i)));
            final Callback<TreeTableColumn<DownloadableEntity, String>, TreeTableCell<DownloadableEntity, String>> cell = properties.get(i).getCell();
            if (cell != null)
                columnFactoryMap.put(columns.get(i), cell);
        }
    }

    private JavaBeanStringProperty getProperty(PropertyDefinition definition) throws NoSuchMethodException {
        return propertyBuilder.name(definition.getName()).getter(definition.getGetter()).setter(definition.getSetter()).build();
    }

    public Map<String, StringProperty> getPropertyMap() {
        return propertyMap;
    }

    public Map<String, Callback<TreeTableColumn<DownloadableEntity, String>, TreeTableCell<DownloadableEntity, String>>> getColumnFactoryMap() {
        return columnFactoryMap;
    }

    public void nullGetter(String getter) {
        throw new UnsupportedOperationException("This property does not support Getter");
    }

    public void nullSetter(String setter) {
        throw new UnsupportedOperationException("This property does not support Setter");
    }

}
