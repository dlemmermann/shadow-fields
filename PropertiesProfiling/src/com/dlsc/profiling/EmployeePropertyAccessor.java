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
    public enum FIELDS {
        name,
        powers,
        supervisor,
        minions
    }
    private final Object[] modelProperties = new Object[FIELDS.values().length];

    public Object[] getModelProperties(){
        return modelProperties;
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