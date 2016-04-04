package com.dlsc.profiling;


import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
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

public class EmployeeProfiling extends Application {

	final List<Employee> employees = new LinkedList<>();
	final List<EmployeeShadowFields> employeesShadowFields = new LinkedList<>();
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

		TableColumn<TestResult, Long> durationAColumn = new TableColumn<>("Duration A");
		durationAColumn.setCellValueFactory(new PropertyValueFactory<>("durationA"));
		durationAColumn.setPrefWidth(100);
		tableView.getColumns().add(durationAColumn);

		TableColumn<TestResult, Long> durationBColumn = new TableColumn<>("Duration B");
		durationBColumn.setCellValueFactory(new PropertyValueFactory<>("durationB"));
		durationBColumn.setPrefWidth(100);
		tableView.getColumns().add(durationBColumn);

		TableColumn<TestResult, Long> memoryAColumn = new TableColumn<>("Memory A");
		memoryAColumn.setCellValueFactory(new PropertyValueFactory<>("memoryA"));
		memoryAColumn.setPrefWidth(100);
		tableView.getColumns().add(memoryAColumn);

		TableColumn<TestResult, Long> memoryBColumn = new TableColumn<>("Memory B");
		memoryBColumn.setCellValueFactory(new PropertyValueFactory<>("memoryB"));
		memoryBColumn.setPrefWidth(100);
		tableView.getColumns().add(memoryBColumn);

		TableColumn<TestResult, Long> memoryDiffColumn = new TableColumn<>("Memory Diff");
		memoryDiffColumn.setCellValueFactory(new PropertyValueFactory<>("memoryDiff"));
		memoryDiffColumn.setPrefWidth(100);
		tableView.getColumns().add(memoryDiffColumn);

		BorderPane.setMargin(tableView, new Insets(10));
		return tableView;
	}

	private void runTests() {
		button.setDisable(true);
		propertiesCheckBox.setDisable(true);

		resultsTable.getItems().clear();

		Platform.runLater(() -> {
			int[] counts = new int[] { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 100000, 1000000, 2000000 };
			for (int c : counts) {
				TestResult result = test(c, propertiesCheckBox.isSelected());
				resultsTable.getItems().add(result);
			}

			button.setDisable(false);
			propertiesCheckBox.setDisable(false);
		});
	}

	private TestResult test(int count, boolean accessProperties) {
		TestResult result = new TestResult();
		result.setCount(count);
		result.setAccessProperties(accessProperties);

		StringBuilder sb = new StringBuilder();
		sb.append("count = " + count);

		// Property Accessor
		employees.clear();
		System.gc();

		long usedSpaceA = getUsedSpace();

		long time = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			Employee employee = new Employee("name", "powers");
			if (accessProperties) {
				employee.nameProperty();
				employee.powersProperty();
				employee.supervisorProperty();
				employee.minionsObservables();
			}
			employees.add(employee);
		}

		result.setDurationA(System.currentTimeMillis() - time);
		sb.append(", time spent: " + result.getDurationA());

		// measure memory
		System.gc();
		long diffA = getUsedSpace() - usedSpaceA;
		result.setMemoryA(diffA);
		employees.clear();

		// Property Accessor

		employeesShadowFields.clear();

		// measure memory
		System.gc();
		long usedSpaceB = getUsedSpace();

		time = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			EmployeeShadowFields employee = new EmployeeShadowFields("name", "powers");
			if (accessProperties) {
				employee.nameProperty();
				employee.powersProperty();
				employee.supervisorProperty();
				employee.getMinions();
			}
			employeesShadowFields.add(employee);
		}

		result.setDurationB(System.currentTimeMillis() - time);
		sb.append(" vs. " + result.getDurationB() + " millis");

		// measure memory
		System.gc();
		long diffB = getUsedSpace() - usedSpaceB;
		result.setMemoryB(diffB);
		employeesShadowFields.clear();

		System.out.println(sb.toString());
		System.out.println("   Memory Diff: " + (diffB - diffA));

		return result;
	}

	private long getUsedSpace() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	public static class TestResult {

		int count;
		boolean accessProperties;
		long durationA;
		long durationB;
		long memoryA;
		long memoryB;

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

		public final long getDurationA() {
			return durationA;
		}

		public final void setDurationA(long durationA) {
			this.durationA = durationA;
		}

		public final long getDurationB() {
			return durationB;
		}

		public final void setDurationB(long durationB) {
			this.durationB = durationB;
		}

		public final long getMemoryA() {
			return memoryA;
		}

		public final void setMemoryA(long memoryA) {
			this.memoryA = memoryA;
		}

		public final long getMemoryB() {
			return memoryB;
		}

		public final void setMemoryB(long memoryB) {
			this.memoryB = memoryB;
		}

		public final long getMemoryDiff() {
			return getMemoryB() - getMemoryA();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
