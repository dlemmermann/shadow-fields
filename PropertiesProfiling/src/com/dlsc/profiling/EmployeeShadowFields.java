package com.dlsc.profiling;


import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmployeeShadowFields {

    public EmployeeShadowFields(String name, String powers) {
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

    public EmployeeShadowFields getSupervisor() {
        return supervisor instanceof ObjectProperty ? ((ObjectProperty<EmployeeShadowFields>)supervisor).get() : (EmployeeShadowFields) supervisor;
    }

    public final void setSupervisor(EmployeeShadowFields supervisor) {
        if (this.supervisor instanceof ObjectProperty)
            ((ObjectProperty<EmployeeShadowFields>)this.supervisor).set(supervisor);
        else
            this.supervisor = supervisor;
    }

    public final ObjectProperty<EmployeeShadowFields> supervisorProperty() {
        if (!(supervisor instanceof ObjectProperty)) {
    		supervisor = new SimpleObjectProperty<EmployeeShadowFields>(this, "supervisor", (EmployeeShadowFields) supervisor);
    	}
    	return (ObjectProperty<EmployeeShadowFields>)supervisor;
    }

    private ObservableList<EmployeeShadowFields> minions;

    public final ObservableList<EmployeeShadowFields> getMinions() {
    	if (minions == null) {
    		minions = FXCollections.observableArrayList();
    	}

    	return minions;
    }

    public final void setMinions(List<EmployeeShadowFields> minions) {
    	getMinions().setAll(minions);
    }
}
