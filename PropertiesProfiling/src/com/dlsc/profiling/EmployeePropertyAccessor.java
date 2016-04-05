package com.dlsc.profiling;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * A hybrid domain and model object using the shadow field pattern to save memory.
 * Created by cpdea
 */
public class EmployeePropertyAccessor implements PropertyAccessors{

    /** This is a map to hold properties and observables */


    public static final String NAME_PROPERTY = "name";
    public static final String POWERS_PROPERTY = "powers";
    public static final String SUPERVISOR_PROPERTY = "supervisor";
    public static final String MINIONS_PROPERTY = "minions";

    public EmployeePropertyAccessor(String name, String powers) {
        setName(name);
        setPowers(powers);
    }


    public final String getName() {
        return getValue(NAME_PROPERTY, "");
    }
    public final void setName(String name) {
        setValue(NAME_PROPERTY, name);
    }
    public final StringProperty nameProperty() {
        return refProperty(NAME_PROPERTY, SimpleStringProperty.class, String.class);
    }

    public String getPowers() {
        return getValue(POWERS_PROPERTY, "");
    }

    public final StringProperty powersProperty() {
        return refProperty(POWERS_PROPERTY, SimpleStringProperty.class, String.class);
    }

    public final void setPowers(String powers) {
        setValue(POWERS_PROPERTY, powers);
    }

    public final EmployeePropertyAccessor getSupervisor() {
        return getValue(SUPERVISOR_PROPERTY, null);
    }

    public final ObjectProperty<EmployeePropertyAccessor> supervisorProperty() {
        return refProperty(SUPERVISOR_PROPERTY, SimpleObjectProperty.class, EmployeePropertyAccessor.class);
    }

    public final void setSupervisor(EmployeePropertyAccessor supervisor) {
        setValue(SUPERVISOR_PROPERTY, supervisor);
    }

    public final List<EmployeePropertyAccessor> getMinions() {
        return getValues(MINIONS_PROPERTY, new ArrayList<>());
    }

    public final ObservableList<EmployeePropertyAccessor> minionsObservables() {
        return refObservables(MINIONS_PROPERTY);
    }

    public final void setMinions(List<EmployeePropertyAccessor> minions) {
        setValues(MINIONS_PROPERTY, minions);
    }

}