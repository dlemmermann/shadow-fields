package com.dlsc.profiling;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import static com.dlsc.profiling.PropertyAccessors.registerFields;

/**
 * A hybrid domain and model object using the PropertyAccessors interface
 * similar Shadow Fields pattern to save memory. Any fields considered to
 * be a raw object type or JavaFX Property will declare a private member
 * as an Object. Next, the developer will declare any enum with entries
 * named the same as the private member variable names.
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
public class EmployeePropertyAccessor implements PropertyAccessors {

    private Object name;
    private Object powers;
    private Object supervisor;
    private Object minions;

    enum FIELDS {
        name,
        powers,
        supervisor,
        minions
    }

    static {
        // register fields one time.
        // (Warning: enum's ordinal value is reassigned and index number)
        registerFields(EmployeePropertyAccessor.class, FIELDS.values());
    }

    public EmployeePropertyAccessor(String name, String powers) {
        setName(name);
        setPowers(powers);
    }

    public final String getName() {
        return getValue(FIELDS.name, "");
    }
    public final void setName(String name) {
        setValue(FIELDS.name, name);
    }
    public final StringProperty nameProperty() {
        return refProperty(FIELDS.name, SimpleStringProperty.class, String.class);
    }

    public String getPowers() {
        return getValue(FIELDS.powers, "");
    }

    public final StringProperty powersProperty() {
        return refProperty(FIELDS.powers, SimpleStringProperty.class, String.class);
    }

    public final void setPowers(String powers) {
        setValue(FIELDS.powers, powers);
    }

    public final EmployeePropertyAccessor getSupervisor() {
        return getValue(FIELDS.supervisor, null);
    }

    public final ObjectProperty<EmployeePropertyAccessor> supervisorProperty() {
        return refProperty(FIELDS.supervisor, SimpleObjectProperty.class, EmployeePropertyAccessor.class);
    }

    public final void setSupervisor(EmployeePropertyAccessor supervisor) {
        setValue(FIELDS.supervisor, supervisor);
    }

    public final List<EmployeePropertyAccessor> getMinions() {
        return getValues(FIELDS.minions, new ArrayList<>());
    }

    public final ObservableList<EmployeePropertyAccessor> minionsObservables() {
        return refObservables(FIELDS.minions);
    }

    public final void setMinions(List<EmployeePropertyAccessor> minions) {
        setValues(FIELDS.minions, minions);
    }

}