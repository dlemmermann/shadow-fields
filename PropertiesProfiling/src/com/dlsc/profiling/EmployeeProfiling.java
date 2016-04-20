package com.dlsc.profiling;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.BiFunction;

public class EmployeeProfiling extends Application {

    private final Map<Class<? extends EmployeeIF>, BiFunction<String, String, EmployeeIF>> testClasses = new LinkedHashMap<>();
    {
        testClasses.put(Employee.class, (name, powers) -> new Employee(name, powers));
        testClasses.put(EmployeePropertyAccessor.class, (name, powers) -> new EmployeePropertyAccessor(name, powers));
        testClasses.put(EmployeeShadowFields.class, (name, powers) -> new EmployeeShadowFields(name, powers));
        testClasses.put(EmployeeObjectFields.class, (name, powers) -> new EmployeeObjectFields(name, powers));
        testClasses.put(EmployeeFXObservable.class, (name, powers) -> new EmployeeFXObservable(name, powers));
    }

	private List<EmployeeIF> employees;

    private Button button;
	private TableView<TestResult> resultsTable;
	private CheckBox propertiesCheckBox;

	@Override
	public void start(Stage primaryStage) throws Exception {
		button = new Button("Start");
		button.setMaxWidth(Double.MAX_VALUE);
		button.setOnAction(evt -> runTests());

		propertiesCheckBox = new CheckBox("Access Properties (causes JavaFX Property creation)");

		VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setFillWidth(true);
		vbox.getChildren().addAll(button, propertiesCheckBox);
		BorderPane.setMargin(vbox, new Insets(10));

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(vbox);

		resultsTable = createTableView();
		borderPane.setCenter(resultsTable);

		Scene scene = new Scene(borderPane);
		primaryStage.setTitle("Employee Profiling");
		primaryStage.setScene(scene);
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	private TableView<TestResult> createTableView() {
		TableView<TestResult> tableView = new TableView<EmployeeProfiling.TestResult>();

        TableColumn<TestResult, Integer> countColumn = new TableColumn<>("Objects");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        countColumn.setPrefWidth(80);
        tableView.getColumns().add(countColumn);

        for (Class<? extends EmployeeIF> type : testClasses.keySet()) {
            TableColumn<TestResult, String> groupingColumn = new TableColumn<>(type.getSimpleName());
            groupingColumn.setPrefWidth(220);
            tableView.getColumns().add(groupingColumn);

            TableColumn<TestResult, Long> durationColumn = new TableColumn<>("Time");
            durationColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<Long>(param.getValue().getDuration(type)));
            durationColumn.setPrefWidth(110);
            groupingColumn.getColumns().add(durationColumn);

            TableColumn<TestResult, String> memColumn = new TableColumn<>("Mem");
            memColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getNiceMemory(type)));
            memColumn.setPrefWidth(110);
            groupingColumn.getColumns().add(memColumn);
        }

		BorderPane.setMargin(tableView, new Insets(10));
		return tableView;
	}

	private void runTests() {
		button.setDisable(true);
		propertiesCheckBox.setDisable(true);

		resultsTable.getItems().clear();

		int[] counts = new int[] { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 100000, 1000000, 2000000 };
		for (int c : counts) {
			System.out.println("Testing count = " + c);
			TestResult result = test(c, propertiesCheckBox.isSelected());
			resultsTable.getItems().add(result);
		}

		button.setDisable(false);
		propertiesCheckBox.setDisable(false);
	}

	private TestResult test(int count, boolean accessProperties) {
		TestResult result = new TestResult();
		result.setCount(count);
		result.setAccessProperties(accessProperties);

        for (Map.Entry<Class<? extends EmployeeIF>, BiFunction<String, String, EmployeeIF>> entry : testClasses.entrySet()) {
			employees = null;
            test(count, accessProperties, result, entry.getKey(), entry.getValue());
			employees = null;
		}

		return result;
	}

    private void test(int count, boolean accessProperties, TestResult result, Class<? extends EmployeeIF> employeeType, BiFunction<String, String, EmployeeIF> employeeCreator) {
        System.gc();
        long usedSpace = getUsedSpace();
        long time = System.currentTimeMillis();

        employees = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            EmployeeIF employee = employeeCreator.apply("name", "powers");
            if (accessProperties) {
                employee.nameProperty();
                employee.powersProperty();
                employee.supervisorProperty();
                employee.getMinions();
            }
            employees.add(employee);
        }

        result.setDuration(employeeType, System.currentTimeMillis() - time);

        // measure memory
        System.gc();
        result.setMemory(employeeType, getUsedSpace() - usedSpace);
    }

	private long getUsedSpace() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	public static class TestResult {

        int count;
        boolean accessProperties;

        Map<Class<? extends EmployeeIF>, Long> duration = new HashMap<>();
        Map<Class<? extends EmployeeIF>, Long> memory = new HashMap<>();

		public final int getCount() {
			return count;
		}

		public final void setCount(int count) {
			this.count = count;
		}

		public final boolean isAccessProperties() {
			return accessProperties;
		}

		public final void setAccessProperties(boolean accessProperties) {
			this.accessProperties = accessProperties;
		}

		public final long getDuration(Class<? extends EmployeeIF> employeeType) {
			return duration.get(employeeType).longValue();
		}

		public final void setDuration(Class<? extends EmployeeIF> employeeType, long duration) {
			this.duration.put(employeeType, Long.valueOf(duration));
		}

		public final long getMemory(Class<? extends EmployeeIF> employeeType) {
			return memory.get(employeeType).longValue();
		}

		public final void setMemory(Class<? extends EmployeeIF> employeeType, long memory) {
			this.memory.put(employeeType, Long.valueOf(memory));
		}

		public final String getNiceMemory(Class<? extends EmployeeIF> employeeType) {
			return humanReadableByteCount(getMemory(employeeType), true);
		}

	}

    public static String humanReadableByteCount(final long bytes, boolean si) {
        final int unit = si ? 1000 : 1024;
        if (bytes < unit)
		 {
			return bytes + " B"; //$NON-NLS-1$
		}
        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre); //$NON-NLS-1$
    }

	public static void main(String[] args) {
		launch(args);
	}
}
