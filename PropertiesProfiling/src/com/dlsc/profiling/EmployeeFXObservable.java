package com.dlsc.profiling;


import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import griffon.transform.FXObservable;

public class EmployeeFXObservable implements EmployeeIF<EmployeeFXObservable> {

    @FXObservable
    private String name;

    @FXObservable
    private String powers;

    @FXObservable
    private EmployeeFXObservable supervisor;

    private ObservableList<EmployeeFXObservable> minions;

    public EmployeeFXObservable(String name, String powers) {
        setName(name);
        setPowers(powers);
    }

    public final ObservableList<EmployeeFXObservable> getMinions() {
        if (minions == null) {
            minions = FXCollections.observableArrayList();
        }

        return minions;
    }

    public final void setMinions(List<EmployeeFXObservable> minions) {
        getMinions().setAll(minions);
    }
}
