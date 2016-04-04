package com.dlsc.profiling;

import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
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
	final List<EmployeePropertyAccessor> employeePropertyAccessors = new LinkedList<>();
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

		TableColumn<TestResult, Long> durationAColumn = new TableColumn<>("Time Standard");
		durationAColumn.setCellValueFactory(new PropertyValueFactory<>("durationStandard"));
		durationAColumn.setPrefWidth(110);
		tableView.getColumns().add(durationAColumn);

		TableColumn<TestResult, Long> memoryStandard = new TableColumn<>("Mem Standard");
		memoryStandard.setCellValueFactory(new PropertyValueFactory<>("niceMemoryStandard"));
		memoryStandard.setPrefWidth(110);
		tableView.getColumns().add(memoryStandard);

		TableColumn<TestResult, Long> durationBColumn = new TableColumn<>("Time Accessor");
		durationBColumn.setCellValueFactory(new PropertyValueFactory<>("durationPropertyAccessor"));
		durationBColumn.setPrefWidth(110);
		tableView.getColumns().add(durationBColumn);

		TableColumn<TestResult, Long> memoryAccessor = new TableColumn<>("Mem Accessor");
		memoryAccessor.setCellValueFactory(new PropertyValueFactory<>("niceMemoryPropertyAccessor"));
		memoryAccessor.setPrefWidth(110);
		tableView.getColumns().add(memoryAccessor);

		TableColumn<TestResult, Long> memoryAColumn = new TableColumn<>("Time Shadow");
		memoryAColumn.setCellValueFactory(new PropertyValueFactory<>("durationShadowFields"));
		memoryAColumn.setPrefWidth(110);
		tableView.getColumns().add(memoryAColumn);


		TableColumn<TestResult, Long> memoryShadow = new TableColumn<>("Mem Shadow");
		memoryShadow.setCellValueFactory(new PropertyValueFactory<>("niceMemoryShadowFields"));
		memoryShadow.setPrefWidth(110);
		tableView.getColumns().add(memoryShadow);

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

		clearLists();
		testStandard(count, accessProperties, result);

		clearLists();
		testPropertyAccessor(count, accessProperties, result);

		clearLists();
		testShadowFields(count, accessProperties, result);

		return result;
	}

	private void clearLists() {
		employees.clear();
		employeePropertyAccessors.clear();
		employeesShadowFields.clear();
		System.gc();
	}

	private void testShadowFields(int count, boolean accessProperties, TestResult result) {
		long usedSpace = getUsedSpace();
		long time = System.currentTimeMillis();

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

		result.setDurationShadowFields(System.currentTimeMillis() - time);

		// measure memory
		System.gc();
		long diff = getUsedSpace() - usedSpace;
		result.setMemoryShadowFields(diff);
		employeesShadowFields.clear();
	}

	private void testPropertyAccessor(int count, boolean accessProperties, TestResult result) {
		long usedSpace = getUsedSpace();
		long time = System.currentTimeMillis();

		for (int i = 0; i < count; i++) {
			EmployeePropertyAccessor employeePropertyAccessor = new EmployeePropertyAccessor("name", "powers");
			if (accessProperties) {
				employeePropertyAccessor.nameProperty();
				employeePropertyAccessor.powersProperty();
				employeePropertyAccessor.supervisorProperty();
				employeePropertyAccessor.minionsObservables();
			}
			employeePropertyAccessors.add(employeePropertyAccessor);
		}

		result.setDurationPropertyAccessor(System.currentTimeMillis() - time);

		// measure memory
		System.gc();
		long diff = getUsedSpace() - usedSpace;
		result.setMemoryPropertyAccessor(diff);
		employeePropertyAccessors.clear();
	}

	private void testStandard(int count, boolean accessProperties, TestResult result) {
		long usedSpace = getUsedSpace();
		long time = System.currentTimeMillis();

		for (int i = 0; i < count; i++) {
			Employee employee = new Employee("name", "powers");
			if (accessProperties) {
				employee.nameProperty();
				employee.powersProperty();
				employee.supervisorProperty();
				employee.getMinions();
			}
			employees.add(employee);
		}

		result.setDurationStandard(System.currentTimeMillis() - time);

		// measure memory
		System.gc();
		long diff = getUsedSpace() - usedSpace;
		result.setMemoryStandard(diff);
		employees.clear();
	}

	private long getUsedSpace() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	public static class TestResult {

		int count;
		boolean accessProperties;

		long durationStandard;
		long durationPropertyAccessor;
		long durationShadowFields;

		long memoryStandard;
		long memoryPropertyAccessor;
		long memoryShadowFields;

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

		public final long getDurationStandard() {
			return durationStandard;
		}

		public final void setDurationStandard(long duration) {
			this.durationStandard = duration;
		}

		public final long getDurationPropertyAccessor() {
			return durationPropertyAccessor;
		}

		public final void setDurationPropertyAccessor(long duration) {
			this.durationPropertyAccessor = duration;
		}

		public final long getDurationShadowFields() {
			return durationShadowFields;
		}

		public final void setDurationShadowFields(long duration) {
			this.durationShadowFields = duration;
		}

		public final long getMemoryStandard() {
			return memoryStandard;
		}

		public final void setMemoryStandard(long memoryStandard) {
			this.memoryStandard = memoryStandard;
		}

		public final long getMemoryPropertyAccessor() {
			return memoryPropertyAccessor;
		}

		public final void setMemoryPropertyAccessor(long memoryPropertyAccessor) {
			this.memoryPropertyAccessor = memoryPropertyAccessor;
		}

		public final long getMemoryShadowFields() {
			return memoryShadowFields;
		}

		public final void setMemoryShadowFields(long memoryShadowFields) {
			this.memoryShadowFields = memoryShadowFields;
		}

		public final String getNiceMemoryStandard() {
			return humanReadableByteCount(getMemoryStandard(), true);
		}

		public final String getNiceMemoryPropertyAccessor() {
			return humanReadableByteCount(getMemoryPropertyAccessor(), true);
		}

		public final String getNiceMemoryShadowFields() {
			return humanReadableByteCount(getMemoryShadowFields(), true);
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
