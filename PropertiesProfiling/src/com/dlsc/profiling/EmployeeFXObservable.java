package com.dlsc.profiling;


import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import griffon.transform.FXObservable;

@FXObservable
public class EmployeeFXObservable implements EmployeeIF<EmployeeFXObservable> {

    private String name;
    private String powers;
    private EmployeeFXObservable supervisor;
    private ObservableList<EmployeeFXObservable> minions;

    public EmployeeFXObservable(String name, String powers) {
        setName(name);
        setPowers(powers);
    }

}
