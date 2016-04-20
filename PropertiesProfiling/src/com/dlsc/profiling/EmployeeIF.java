package com.dlsc.profiling;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public interface EmployeeIF<T extends EmployeeIF> {

	public String getName();

	public void setName(String name);

	public StringProperty nameProperty();

	public String getPowers();

	public void setPowers(String powers);

	public StringProperty powersProperty();

	public T getSupervisor();

	public void setSupervisor(T supervisor);

	public ObjectProperty<T> supervisorProperty();

	public ObservableList<T> getMinions();
}