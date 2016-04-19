package com.dlsc.profiling;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The PropertyAccessors is a static utility class that provides default methods to
 * support the similar capability of the shadow fields pattern. To save memory object
 * values don't have to be wrapped into a Property object when using getters and
 * setters, however when calling property type methods values will be wrapped into a
 * property object.
 * <pre>
 * Version notes:
 *  Version 1 No private attributes, but a map to hold values. It was expensive.
 *  Version 2 Centralized a map to hold values.
 *  Version 3 Uses reflection to generate index fields and dynamically create property types.
 *  Version 4 Removes the need of reflection. Also, added a convenience method to cast.
 *  Version 5 Converted this interface into a static utility class.
 * </pre>
 *
 * This API allows the developer to easily specify fields without having boilerplate code
 * when creating shadow fields to save memory. Any fields considered to be a raw object type
 * or JavaFX Property will declare a private member as an Object. Next, the developer will
 * declare any enum with entries named the same as the private member variable names. After
 * the fields have been declared simply register the fields using the static method
 * registerFields().
 *
 * The following example is what a user of the API will need to do:
 *
 * <pre>
 *     <code>
 . The rest of the class definition
 *        // STEP 1: static import the PropertyAccessors utility class
 *     import static com.carlfx.PropertyAccessors.*;
 *     public class MyClass  {
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
public class PropertyAccessors {
    private PropertyAccessors() {}
    /**
     * Convenience function to reduce boiler plate of casting objects to return an object.
     * @param object The object to cast.
     * @param <T> The return object of the type T.
     * @return Object of type T
     */
    public static <T> T cast(Object object) {
        return (T) object;
    }

    /**
     * Returns a value from a property object or the raw type.
     * @param p The potential property object.
     * @param <T> The raw value is returned.
     * @return Object value raw type.
     */
    public static <T> T getValue(Object p) {
        return (T) ((p instanceof Property) ? ((Property) p).getValue(): p);
    }

    /**
     * This method will return a Property or raw object type value for the caller to set.
     * <pre>
     *     The following is how to call the setValue() method. Remember to set the attribute to the setValue()
     *     method's return value.
     *     <code>
     *        public final void setMyBrain((String myBrain) {
     *           this.myBrain = setValue(this.myBrain, myBrain);
     *        }
     *     </code>
     * </pre>
     * @param p The potential property object.
     * @param value The raw value to set. If underlying object is a property the raw value will be set into.
     * @param <T> The value type either raw or a property.
     * @return A property or raw object is returned for the caller to set. Important that the caller sets
     * the private member to the return value.
     */
    public static <T> T setValue(Object p, Object value) {
        if (p instanceof Property) {
            ((Property)p).setValue(value);
            return (T) p;
        } else {
            return (T) value;
        }
    }

    /**
     * This method will return a Property type value for the caller to set.
     * <pre>
     *     The following is how to call the refProperty() method. Remember to set the attribute
     *     to the refProperty() method's return value. A convenience method to cast the property as
     *     the return type (syntactic sugar).
     *     <code>
     *        public final StringProperty myBrainProperty() {
     *           myBrain = refProperty(myBrain, SimpleStringProperty.class);
     *           return cast(myBrain);
     *        }
     *     </code>
     * </pre>
     * @param name Name of the property
     * @param p potential callers attribute value (raw or a property)
     * @param propertyClass The concreate JavaFX property class such as SimpleStringProperty.class
     * @param <T> The Property object for the caller to set as.
     * @return The return of the property object.
     */
    public static <T> T refProperty(Object bean, String name, Object p, Class propertyClass) {
        Property prop = null;
        if (p == null || !(p instanceof Property)) {
            // create a property object
            if (SimpleBooleanProperty.class == propertyClass) {
                prop = new SimpleBooleanProperty(bean, name);
            } else if (SimpleDoubleProperty.class == propertyClass) {
                prop = new SimpleDoubleProperty(bean, name);
            } else if (SimpleFloatProperty.class == propertyClass) {
                prop = new SimpleFloatProperty(bean, name);
            } else if (SimpleIntegerProperty.class == propertyClass) {
                prop = new SimpleIntegerProperty(bean, name);
            } else if (SimpleLongProperty.class == propertyClass) {
                prop = new SimpleLongProperty(bean, name);
            } else if (SimpleObjectProperty.class == propertyClass) {
                prop = new SimpleObjectProperty(bean, name);
            } else if (SimpleStringProperty.class == propertyClass) {
                prop = new SimpleStringProperty(bean, name);
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

    public static <T> ObservableList<T> refObservableList(List list) {

        if (list == null) {
            list = FXCollections.observableArrayList();
        } else if (! (list instanceof ObservableList)) {
            list = FXCollections.observableArrayList(list);
        }

        return cast(list);
    }

    public static <K,V> ObservableMap<K, V> refObservableMap(Map<K,V> map) {

        if (map == null) {
            return FXCollections.observableHashMap();
        } else if (! (map instanceof ObservableMap)) {
            ObservableMap<K, V> newMap = FXCollections.observableHashMap();
            newMap.putAll(map);
            return newMap;
        }

        return cast(map);
    }
    public static <E> ObservableSet<E> refObservableSet(Set<E> set) {

        if (set == null) {
            return FXCollections.observableSet(new HashSet<>());
        } else if (! (set instanceof ObservableSet)) {
            ObservableSet<E> newSet = FXCollections.observableSet(new HashSet<>());
            newSet.addAll(set);
            return newSet;
        }
        return cast(set);
    }

}
