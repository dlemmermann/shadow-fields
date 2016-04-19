package com.dlsc.profiling;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.List;

import static com.dlsc.profiling.PropertyAccessors.*;

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
 *     }
 *     </code>
 * </pre>
 *
 *
 * Created by Carl Dea
 */
public class EmployeePropertyAccessor {

    private Object name;
    private Object powers;
    private Object supervisor;
    private List<EmployeePropertyAccessor> minions;

    public EmployeePropertyAccessor(String name, String powers) {
        setName(name);
        setPowers(powers);
    }

    public final String getName() {return getValue(name); }
    public final void setName(String name) { this.name = setValue(this.name, name); }
    public final StringProperty nameProperty() {
        name = refProperty(this, "name", name, SimpleStringProperty.class);
        return cast(name);
    }

    public String getPowers() {
        return getValue(powers);
    }

    public final StringProperty powersProperty() {
        powers = refProperty(this, "powers", powers, SimpleStringProperty.class);
        return cast(powers);
    }

    public final void setPowers(String powers) {
        this.powers = setValue(this.powers, powers);
    }

    public final EmployeePropertyAccessor getSupervisor() {
        return getValue(supervisor);
    }

    public final ObjectProperty<EmployeePropertyAccessor> supervisorProperty() {
        supervisor = refProperty(this, "supervisor", supervisor, SimpleObjectProperty.class);
        return cast(supervisor);
    }

    public final void setSupervisor(EmployeePropertyAccessor supervisor) {
        this.supervisor = setValue(this.supervisor, supervisor);
    }

    public final ObservableList<EmployeePropertyAccessor> getMinions() {
        minions = refObservableList(minions);
        return cast(minions);
    }

    public final void setMinions(List<EmployeePropertyAccessor> minions) {
        getMinions().setAll(minions);
    }
}