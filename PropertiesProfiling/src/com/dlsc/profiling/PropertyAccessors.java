package com.dlsc.profiling;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    Map<String, Object> getModelProperties();

    default <T> T getValue(String name, Object defaultVal) {
        Object p = getModelProperties().get(name);
        p = p==null ? defaultVal : p;
        return (T) ((p instanceof Property) ? ((Property) p).getValue(): p);
    }

    default void setValue(String name, Object value) {
        Object p = getModelProperties().get(name);
        if (p instanceof Property) {
            ((Property)p).setValue(value);
        } else {
            getModelProperties().put(name, value);
        }
    }

    default <T> T refProperty(String name, Class propClass, Class rawValType) {
        Object p = getModelProperties().get(name);
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
            getModelProperties().put(name, prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) prop;
    }

    default <T> List<T> getValues(String name, List<T> defaultValue) {
        Object p, o = getModelProperties().get(name);
        p = o;
        o = o==null ? defaultValue : o;
        if (!o.equals(p)) {
            getModelProperties().put(name, o);
        }
        return  (List<T>) o;
    }

    default <T> void setValues(String name, List<T> newList) {
        Object list = getModelProperties().get(name);
        if (list == null || !(list instanceof ObservableList)) {
            getModelProperties().put(name, newList);
        } else {
            // Should the list be totally replaced? below clears and adds all items
            ObservableList<T> observableList = (ObservableList<T>) list;
            observableList.clear();
            observableList.addAll(newList);
        }
    }

    default <T> ObservableList<T> refObservables(String name) {
        List list = (List) getModelProperties().get(name);

        if (list == null) {
            list = FXCollections.observableArrayList(getValues(name, new ArrayList<>()));
            getModelProperties().put(name, list);
        }

        if (! (list instanceof ObservableList)) {
            list = FXCollections.observableArrayList(list);
            getModelProperties().put(name, list);
        }

        return (ObservableList<T>) list;
    }
}