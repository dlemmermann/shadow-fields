package com.dlsc.profiling;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class EmployeeObjectFields implements EmployeeIF<EmployeeObjectFields> {

    public EmployeeObjectFields(String name, String powers) {
        setName(name);
        setPowers(powers);
    }

    private Object name;

    public final String getName() {
    	return name instanceof StringProperty ? ((StringProperty)name).get() : (String) name;
    }

    public final void setName(String name) {
    	if (this.name instanceof StringProperty)
            ((StringProperty)this.name).set(name);
        else
            this.name = name;
    }

    public final StringProperty nameProperty() {
    	if (!(name instanceof StringProperty)) {
    		name = new SimpleStringProperty(this, "name", (String)name);
    	}
    	return (StringProperty)name;
    }

    private Object powers;

    public String getPowers() {
        return powers instanceof StringProperty ? ((StringProperty)powers).get() : (String) powers;
    }

    public final void setPowers(String powers) {
        if (this.powers instanceof StringProperty)
            ((StringProperty)this.powers).set(powers);
        else
            this.powers = powers;
    }

    public final StringProperty powersProperty() {
        if (!(powers instanceof StringProperty)) {
    		powers = new SimpleStringProperty(this, "powers", (String)powers);
    	}
    	return (StringProperty)powers;
    }

    private Object supervisor;

    public EmployeeObjectFields getSupervisor() {
        return supervisor instanceof ObjectProperty ? ((ObjectProperty<EmployeeObjectFields>)supervisor).get() : (EmployeeObjectFields) supervisor;
    }

    public final void setSupervisor(EmployeeObjectFields supervisor) {
        if (this.supervisor instanceof ObjectProperty)
            ((ObjectProperty<EmployeeObjectFields>)this.supervisor).set(supervisor);
        else
            this.supervisor = supervisor;
    }

    public final ObjectProperty<EmployeeObjectFields> supervisorProperty() {
        if (!(supervisor instanceof ObjectProperty)) {
    		supervisor = new SimpleObjectProperty<EmployeeObjectFields>(this, "supervisor", (EmployeeObjectFields) supervisor);
    	}
    	return (ObjectProperty<EmployeeObjectFields>)supervisor;
    }

    private ObservableList<EmployeeObjectFields> minions;

    public final ObservableList<EmployeeObjectFields> getMinions() {
    	if (minions == null) {
    		minions = FXCollections.observableArrayList();
    	}

    	return minions;
    }

    public final void setMinions(List<EmployeeObjectFields> minions) {
    	getMinions().setAll(minions);
    }
}
