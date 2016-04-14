package com.dlsc.profiling;


import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EmployeeShadowFields implements EmployeeIF<EmployeeShadowFields> {

    public EmployeeShadowFields(String name, String powers) {
        setName(name);
        setPowers(powers);
    }

    private String _name;

    private StringProperty name;

    public final String getName() {
    	return name == null ? _name : name.get();
    }

    public final void setName(String name) {
    	if (this.name == null) {
    		_name = name;
    	} else {
    		this.name.set(name);
    	}
    }

    public final StringProperty nameProperty() {
    	if (name == null) {
    		name = new SimpleStringProperty(this, "name", _name);
			_name = null;
    	}

    	return name;
    }

    private String _powers;

    private StringProperty powers;

    public String getPowers() {
    	return powers == null ? _powers : powers.get();
    }

    public final StringProperty powersProperty() {
    	if (powers == null) {
    		powers = new SimpleStringProperty(this, "powers", _powers);
			_powers = null;
    	}

    	return powers;
    }

    public final void setPowers(String powers) {
    	if (this.powers == null) {
    		_powers = powers;
    	} else {
    		this.powers.set(powers);
    	}
    }

    private EmployeeShadowFields _supervisor;

    private ObjectProperty<EmployeeShadowFields> supervisor;

    public final EmployeeShadowFields getSupervisor() {
    	return supervisor == null ? _supervisor : supervisor.get();
    }

    public final ObjectProperty<EmployeeShadowFields> supervisorProperty() {
    	if (supervisor == null) {
    		supervisor = new SimpleObjectProperty<>(this, "supervisor", _supervisor);
			_supervisor = null;
    	}

    	return supervisor;
    }

    public final void setSupervisor(EmployeeShadowFields supervisor) {
    	if (this.supervisor == null) {
    		_supervisor = supervisor;
    	} else {
    		this.supervisor.set(supervisor);
    	}
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
