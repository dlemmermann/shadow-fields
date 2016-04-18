package com.dlsc.profiling;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Map;

/**
 * The PropertyAccessors interface provides default methods to support the similar
 * capability of the shadow fields pattern. To save memory object values don't have to be
 * wrapped into a Property object when using getters and setters, however when
 * calling property type methods values will be wrapped into a property object.
 *
 * #Version 1 no attributes, but a map to hold values. It was expensive.
 * #Version 2 Centralized a map to hold values.
 * #Version 3 used to use reflection to index fields and dynamically create property types.
 * #Version 4 now removes the need of reflection. Also, added a convenience
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
 . The rest of the class definition
 *        // STEP 1: Implement the interface PropertyAccessors
 *     public class MyClass implements PropertyAccessors {
 *
 *        // STEP 2: Declare your private member (to hold either a native or property type)
 *        private Object myBrain;
 *
 *        // STEP 3: Use the PropertyAccessor interface API for getters/setters/property methods.
 *        public final String getMyBrain() {
 *           return getValue(myBrain);
 *        }
 *        public final void setMyBrain((String myBrain) {
 *           this.myBrain = setValue(this.myBrain, myBrain);
 *        }
 *        public final StringProperty myBrainProperty() {
 *           myBrain = refProperty(myBrain, SimpleStringProperty.class);
 *           return cast(myBrain);
 *        }
 *
 *        // .. The rest of the class definition
 *     </code>
 * </pre>
 *
 * Created by Carl Dea
 */
public interface PropertyAccessors {
    /**
     * Convenience function to reduce boiler plate of casting objects to return an object.
     * @param object The object to cast.
     * @param <T> The return object of the type T.
     * @return Object of type T
     */
    default <T> T cast(Object object) {
        return (T) object;
    }

    /**
     * Returns a value from a property object or the raw type.
     * @param p The potential property object.
     * @param <T> The raw value is returned.
     * @return Object value raw type.
     */
    default <T> T getValue(Object p) {
        return (T) ((p instanceof Property) ? ((Property) p).getValue(): p);
    }

    /**
     * This will return a Property or Raw object value for the caller to set.
     * @param p The potential property object.
     * @param value The raw value to set. If underlying object is a property the raw value will be set into.
     * @param <T> The value type either raw or a property.
     * @return A property or raw object is returned for the caller to set. Important that the caller sets
     * the private member to the return value.
     */
    default <T> T setValue(Object p, Object value) {
        if (p instanceof Property) {
            ((Property)p).setValue(value);
            return (T) p;
        } else {
            return (T) value;
        }

    }

    /**
     *
     * @param name Name of the property
     * @param p potential callers attribute value (raw or a property)
     * @param propertyClass The concreate JavaFX property class such as SimpleStringProperty.class
     * @param <T> The Property object for the caller to set as.
     * @return The return of the property object.
     */
    default <T> T refProperty(String name, Object p, Class propertyClass) {
        Property prop = null;
        if (p == null || !(p instanceof Property)) {
            // create a property object
            if (SimpleBooleanProperty.class == propertyClass) {
                prop = new SimpleBooleanProperty(this, name);
            } else if (SimpleDoubleProperty.class == propertyClass) {
                prop = new SimpleDoubleProperty(this, name);
            } else if (SimpleFloatProperty.class == propertyClass) {
                prop = new SimpleFloatProperty(this, name);
            } else if (SimpleIntegerProperty.class == propertyClass) {
                prop = new SimpleIntegerProperty(this, name);
            } else if (SimpleLongProperty.class == propertyClass) {
                prop = new SimpleLongProperty(this, name);
            } else if (SimpleObjectProperty.class == propertyClass) {
                prop = new SimpleObjectProperty(this, name);
            } else if (SimpleStringProperty.class == propertyClass) {
                prop = new SimpleStringProperty(this, name);
            } else {
                throw new RuntimeException("Unsupported concrete Property class " + propertyClass.getName());
            }
        }

        if (! (p instanceof Property)) {
            prop.setValue(p);
        } else {
            prop = (Property) p;
        }
        return (T) prop;
    }

// @TODO update API to support Simple Lists, Maps and Sets
//                SimpleListProperty
//                SimpleMapProperty
//                SimpleSetProperty
//

    default <T> ObservableList<T> refObservables(List list) {

        if (list == null) {
            list = FXCollections.observableArrayList();
        } else if (! (list instanceof ObservableList)) {
            list = FXCollections.observableArrayList(list);
        }

        return (ObservableList<T>) list;
    }

    default <K,V> ObservableMap<K, V> refObservableMap(Map<K,V> map) {

        if (map == null) {
            return FXCollections.observableHashMap();
        } else if (! (map instanceof ObservableMap)) {
            ObservableMap<K, V> newMap = FXCollections.observableHashMap();
            newMap.putAll(map);
            return newMap;
        }

        return (ObservableMap<K, V>) map;
    }

}
