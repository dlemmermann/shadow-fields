package com.dlsc.profiling;


import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

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

    Object[] getModelProperties();

    default <T> T getValue(Enum name, Object defaultVal) {
        Object p = getModelProperties()[name.ordinal()];
        p = p==null ? defaultVal : p;
        return (T) ((p instanceof Property) ? ((Property) p).getValue(): p);
    }

    default void setValue(Enum name, Object value) {
        Object p = getModelProperties()[name.ordinal()];
        if (p instanceof Property) {
            ((Property)p).setValue(value);
        } else {
            getModelProperties()[name.ordinal()] = value;
        }
    }

    default <T> T refProperty(Enum name, Class propClass, Class rawValType) {
        Object p = getModelProperties()[name.ordinal()];
        Property prop = null;

        try {

            if (p == null) {
                Class[] constructorTypes =
                        new Class[]{Object.class, String.class};
                Constructor<Property> propConstr =
                        propClass.getDeclaredConstructor(constructorTypes);
                prop = propConstr.newInstance(this, name.toString());
            } else if (rawValType.isInstance(p)) {
                Class[] constructorTypes = new Class[]{Object.class,
                        String.class, rawValType};
                Constructor<Property> propConstr =
                        propClass.getDeclaredConstructor(constructorTypes);
                prop = propConstr.newInstance(this, name.toString(), p);
            } else {
                prop = (Property) p;
            }
            getModelProperties()[name.ordinal()] = prop;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) prop;
    }

    default <T> List<T> getValues(Enum name, List<T> defaultValue) {
        Object p, o = getModelProperties()[name.ordinal()];
        p = o;
        o = o==null ? defaultValue : o;
        if (!o.equals(p)) {
            getModelProperties()[name.ordinal()] = o;
        }
        return  (List<T>) o;
    }

    default <T> void setValues(Enum name, List<T> newList) {

        Object list = getModelProperties()[name.ordinal()];
        if (list == null || (list instanceof ArrayList)) {
            getModelProperties()[name.ordinal()] = newList;
        } else {
            // Should the list be totally replaced? below clears and adds all items
            ObservableList<T> observableList = (ObservableList<T>) list;
            observableList.clear();
            observableList.addAll(newList);
        }
    }

    default <T> ObservableList<T> refObservables(Enum name) {

        List list = (List) getModelProperties()[name.ordinal()];

        if (list == null) {
            list = FXCollections.observableArrayList();
            getModelProperties()[name.ordinal()] = list;
        } else if (! (list instanceof ObservableList)) {
            list = FXCollections.observableArrayList(list);
            getModelProperties()[name.ordinal()] = list;
        }

        return (ObservableList<T>) list;
    }
}