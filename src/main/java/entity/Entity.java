package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Entity extends RecursiveTreeObject<Entity> implements Serializable {

    private static final long serialVersionUID = 505L;

    protected static String NULL_GETTER = "nullGetter";
    protected static String NULL_SETTER = "nullSetter";
    private transient final JavaBeanStringPropertyBuilder propertyBuilder = JavaBeanStringPropertyBuilder.create().bean(this);
    protected transient Map<String, StringProperty> propertyMap = new HashMap<>();
    protected transient Map<String, Callback<TreeTableColumn<Entity, String>, TreeTableCell<Entity, String>>> columnFactoryMap = new HashMap<>();

    protected Entity() {
        try {
            bindProperty();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
            final Callback<TreeTableColumn<Entity, String>, TreeTableCell<Entity, String>> cell = properties.get(i).getCell();
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

    public Map<String, Callback<TreeTableColumn<Entity, String>, TreeTableCell<Entity, String>>> getColumnFactoryMap() {
        return columnFactoryMap;
    }

    public void nullGetter(String getter) {
        throw new UnsupportedOperationException("This property does not support Getter");
    }

    public void nullSetter(String setter) {
        throw new UnsupportedOperationException("This property does not support Setter");
    }

}
