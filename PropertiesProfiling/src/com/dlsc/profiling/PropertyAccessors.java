package com.dlsc.profiling;


import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provide default methods to support the similar
 * capability of the shadow fields pattern.
 * To save memory object values don't have to be
 * wrapped into a Property object when using getters
 * and setters, however when calling property type methods
 * values will be wrapped into a property object.
 *
 * Default methods for Observable lists are provided too.
 *
 * Created by cpdea on 4/3/16.
 */
public interface PropertyAccessors {

    Map<Integer, Object> modelProperties = new HashMap<>();
    default Map<Integer, Object> getModelProperties() {
        return modelProperties;
    }

    default <T> T getValue(String name, Object defaultVal) {
        int id = (this.hashCode() + name).hashCode();
        Object p = getModelProperties().get(id);
        p = p==null ? defaultVal : p;
        return (T) ((p instanceof Property) ? ((Property) p).getValue(): p);
    }

    default void setValue(String name, Object value) {
        int id = (this.hashCode() + name).hashCode();
        Object p = getModelProperties().get(id);
        if (p instanceof Property) {
            ((Property)p).setValue(value);
        } else {
            getModelProperties().put(id, value);
        }
    }

    default <T> T refProperty(String name, Class propClass, Class rawValType) {
        int id = (this.hashCode() + name).hashCode();
        Object p = getModelProperties().get(id);
        Property prop = null;

        try {

            if (p == null) {
                Class[] constructorTypes =
                        new Class[]{Object.class, String.class};
                Constructor<Property> propConstr =
                        propClass.getDeclaredConstructor(constructorTypes);
                prop = propConstr.newInstance(this, name);
            } else if (rawValType.isInstance(p)) {
                Class[] constructorTypes = new Class[]{Object.class,
                        String.class, rawValType};
                Constructor<Property> propConstr =
                        propClass.getDeclaredConstructor(constructorTypes);
                prop = propConstr.newInstance(this, name, p);
            } else {
                prop = (Property) p;
            }
            getModelProperties().put(id, prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) prop;
    }

    default <T> List<T> getValues(String name, List<T> defaultValue) {
        int id = (this.hashCode() + name).hashCode();
        Object p, o = getModelProperties().get(id);
        p = o;
        o = o==null ? defaultValue : o;
        if (!o.equals(p)) {
            getModelProperties().put(id, o);
        }
        return  (List<T>) o;
    }

    default <T> void setValues(String name, List<T> newList) {

        int id = (this.hashCode() + name).hashCode();
        Object list = getModelProperties().get(id);
        if (list == null || !(list instanceof ObservableList)) {
            getModelProperties().put(id, newList);
        } else {
            // Should the list be totally replaced? below clears and adds all items
            ObservableList<T> observableList = (ObservableList<T>) list;
            observableList.clear();
            observableList.addAll(newList);
        }
    }

    default <T> ObservableList<T> refObservables(String name) {
        int id = (this.hashCode() + name).hashCode();
        List list = (List) getModelProperties().get(id);

        if (list == null) {
            list = FXCollections.observableArrayList(getValues(name, new ArrayList<>()));
            getModelProperties().put(id, list);
        }

        if (! (list instanceof ObservableList)) {
            list = FXCollections.observableArrayList(list);
            getModelProperties().put(id, list);
        }

        return (ObservableList<T>) list;
    }
}