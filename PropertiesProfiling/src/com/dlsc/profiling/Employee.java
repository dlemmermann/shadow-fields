package com.dlsc.profiling;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Employee implements EmployeeIF<Employee> {

	public Employee(String name, String powers) {
		setName(name);
		setPowers(powers);
	}

	private StringProperty name = new SimpleStringProperty(this, "name");

	public final String getName() {
		return name.get();
	}

	public final void setName(String name) {
		this.name.set(name);
	}

	public final StringProperty nameProperty() {
		return name;
	}

	private StringProperty powers = new SimpleStringProperty(this, "powers");

	public String getPowers() {
		return powers.get();
	}

	public final StringProperty powersProperty() {
		return powers;
	}

	public final void setPowers(String powers) {
		this.powers.set(powers);
	}

	private ObjectProperty<Employee> supervisor = new SimpleObjectProperty<>(this, "supervisor");

	public final Employee getSupervisor() {
		return supervisor.get();
	}

	public final ObjectProperty<Employee> supervisorProperty() {
		return supervisor;
	}

	public final void setSupervisor(Employee supervisor) {
		this.supervisor.set(supervisor);
	}

	private ObservableList<Employee> minions;

	public final ObservableList<Employee> getMinions() {
		if (minions == null) {
			minions  = FXCollections.observableArrayList();
		}
		return minions;
	}

	public final void setMinions(List<Employee> minions) {
		getMinions().setAll(minions);
	}
}