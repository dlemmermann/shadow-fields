package com.dlsc.profiling;


import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * The PropertyAccessors interface provides default methods to support the similar
 * capability of the shadow fields pattern. To save memory object values don't have to be
 * wrapped into a Property object when using getters and setters, however when
 * calling property type methods values will be wrapped into a property object.
 *
 * This API allows the developer to easily specify fields without having boilerplate code
 * when creating shadow fields to save memory. Any fields considered to
 * be a raw object type or JavaFX Property will declare a private member
 * as an Object. Next, the developer will declare any enum with entries
 * named the same as the private member variable names. After the fields have been
 * declared simply register the fields using the static method registerFields().
 *
 * The following example is what a user of the API will need to do:
 * <pre>
 *     <code>
 *        // STEP 1: Implement the interface PropertyAccessors
 *     public class MyClass implements PropertyAccessors {
 *
 *        // STEP 2: Declare your private member (to hold either a native or property type)
 *        private Object myBrain;
 *
 *        // STEP 3: Choose the fields to be considered properties. Name entries to be the same as member variables!
 *        enum FIELDS {
 *           myBrain
 *        }
 *
 *        // STEP 4: Register the fields to be shadowed.
 *        static {
 *           registerFields(MyClass.class, FIELDS.values());
 *        }
 *
 *        // STEP 5: Use the PropertyAccessor interface API for getters/setters/property methods.
 *        public final String getMyBrain() {
 *           return getValue(FIELDS.myBrain, "");
 *        }
 *        public final void setMyBrain((String myBrain) {
 *           setValue(FIELDS.myBrain, myBrain);
 *        }
 *        public final StringProperty myBrainProperty() {
 *           return refProperty(FIELDS.myBrain, SimpleStringProperty.class, String.class);
 *        }
 *
 *        // .. The rest of the class definition
 *     }
 *     </code>
 * </pre>
 *
 * Created by Carl Dea
 */
public interface PropertyAccessors {

    List<Field> fieldList = new ArrayList<>();
    List<Constructor> constructorList = new ArrayList<>();
    Field enumOrdinalField = getEnumOrdinalField();

    Class[] constructorTypesA = new Class[]{Object.class, String.class};
    Class[] constructorTypesB = new Class[]{Object.class, String.class, null};

    static Field getEnumOrdinalField() {
        try {
            return Enum.class.getDeclaredField("ordinal");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
    static void registerFields(Class clazz, Enum[] enumFields) {
        //synchronized (fieldList) {
        enumOrdinalField.setAccessible(true);

        for(Enum enumField:enumFields) {
                try {
                    Field field = clazz.getDeclaredField(enumField.toString());
                    field.setAccessible(true);
                    enumOrdinalField.set(enumField, fieldList.size() );
                    fieldList.add(field);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                constructorList.add(null);
            }
        enumOrdinalField.setAccessible(false);

        //}
    }

    default Object getFieldValue(Enum name) {
        try {
            Field field = fieldList.get(name.ordinal());
            Object p = field.get(this);
            return p;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    default void setFieldValue(Enum name, Object value) {
        try {
            Field field = fieldList.get(name.ordinal());
            field.set(this, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    default <T> T getValue(Enum name, Object defaultVal) {
        Object p = null;
        try {
            Field field = fieldList.get(name.ordinal());
            p = field.get(this);
            p = p==null ? defaultVal : p;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) ((p instanceof Property) ? ((Property) p).getValue(): p);
    }

    default void setValue(Enum name, Object value) {
        Object p = getFieldValue(name);
        if (p instanceof Property) {
            ((Property)p).setValue(value);
        } else {
            setFieldValue(name, value);
        }
    }
    default <T> T refProperty(Enum name, Class propClass, Class rawValType) {
        Object p = getFieldValue(name);
        Property prop = null;
        try {
            if (p == null) {

                Constructor<Property> propConstr = constructorList.get(name.ordinal());
                if (propConstr == null) {
                    propConstr = propClass.getDeclaredConstructor(constructorTypesA);
                    constructorList.set(name.ordinal(), propConstr);
                }
                prop = propConstr.newInstance(this, name.name());
            } else if (rawValType.isInstance(p)) {
                constructorTypesB[2] = rawValType;
                Constructor<Property> propConstr = constructorList.get(name.ordinal());
                if (propConstr == null) {
                    propConstr = propClass.getDeclaredConstructor(constructorTypesB);
                    constructorList.set(name.ordinal(), propConstr);
                }

                prop = propConstr.newInstance(this, name.name(), p);
            } else {
                prop = (Property) p;
            }
            setFieldValue(name, prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) prop;
    }

    default <T> List<T> getValues(Enum name, List<T> defaultValue) {
        Object p, o = getFieldValue(name);
        p = o;
        o = o==null ? defaultValue : o;
        if (!o.equals(p)) {
            setFieldValue(name, o);
        }
        return  (List<T>) o;
    }

    default <T> void setValues(Enum name, List<T> newList) {

        Object list = getFieldValue(name);
        if (list == null || (list instanceof ArrayList)) {
            setFieldValue(name, newList);
        } else {
            // Should the list be totally replaced? below clears and adds all items
            ObservableList<T> observableList = (ObservableList<T>) list;
            observableList.clear();
            observableList.addAll(newList);
        }
    }

    default <T> ObservableList<T> refObservables(Enum name) {

        List list = (List) getFieldValue(name);

        if (list == null) {
            list = FXCollections.observableArrayList();
            setFieldValue(name, list);
        } else if (! (list instanceof ObservableList)) {
            list = FXCollections.observableArrayList(list);
            setFieldValue(name, list);
        }

        return (ObservableList<T>) list;
    }
}