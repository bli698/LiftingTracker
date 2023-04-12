package shbli.LiftingTracker;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DisplayController implements Initializable {

	@FXML GridPane gridpane; 
	@FXML ComboBox<String> dropDown;
	@FXML TextField repsInput;
	@FXML TextField weightInput;
	@FXML CategoryAxis xAxis;
	@FXML NumberAxis yAxis;
	@FXML LineChart<String, Number> chart;
	List<String> exercises = new ArrayList<String>();
	int numExercises = 0;
	Connection connection;
	Statement statement;
	ResultSet result;

	// Use Brzycki Formula to calculate power based on reps and weight
	// Weight * (36 / (37 - reps)) = 1 rep max (aka Power)
	
	
	// Functionality: Accesses the database table associated with the selected exercise
	// 				 Plots data points and sets the title of the chart 
	public void updateChart() {
		String eName = dropDown.getValue();
		
		// SQL query to get data from table
		String query = "select * from " + eName;
		try {
			result = statement.executeQuery(query);
		} catch (SQLException err) {
			err.printStackTrace();
		}
		chart.setTitle(eName);
		int reps;
		double weight, power;
		String date;
		// cycles through the data, adding it to a series as data points
		try {
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName("Your Impressive Growth");
			while(result.next()) {
				reps = result.getInt(1);
				weight = result.getDouble(2);
				date = result.getString(3);
				power =  1.0 * weight * 36 / (37 - reps);
				power = Math.round(power * 100.0) / 100.0;
				series.getData().add(new XYChart.Data<String, Number>(date, power));
			}
			
			// clears the chart of any previous series, then adds newly made series
			chart.getData().clear();
			chart.getData().add(series);
			
		} catch (SQLException err) {
			err.printStackTrace();
		}
		
		

		
	}
	
	// ORDER OF TABLES: (reps INT, weight DOUBLE, ymd DATE)
	// DONE
	public void addNewData(ActionEvent e) {
		// if combobox is editable, that means we are adding a new exercise
		String query, eName;
		eName = dropDown.getValue();
		if (dropDown.isEditable()) {
			// add new exercise to the database
			exercises.add(numExercises, eName);
			query = "create table " + eName + " (reps INT, weight DOUBLE, ymd DATE);";
			try {
				statement.execute(query);
			} catch (SQLException err) {
				err.printStackTrace();
			}
			// updates the drop down menu to contain this new exercise
			dropDown.setItems(FXCollections.observableArrayList(exercises));
			dropDown.setEditable(false);
		}
		
		// adds reps, weight, and today's date to the existing database
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime now = LocalDateTime.now();
		if (repsInput.getLength() > 0 && weightInput.getLength() > 0) {
			int reps = Integer.parseInt(repsInput.getText());
			double weight = Double.parseDouble(weightInput.getText());
			
			// adding new data into the database
			query = "INSERT INTO " + eName + "\nvalues(" + reps + ", " + weight + ", \"" + dtf.format(now) + "\")";
			
			try {
				statement.executeUpdate(query);
			} catch (SQLException err) {
				err.printStackTrace();
			}
			
			// adding new data point to the currently displayed graph
			updateChart();
		}
	}
	
	public void exerciseSelected(ActionEvent e) {
		String eName;
		if ((eName = dropDown.getValue()) == "New Exercise") {
			dropDown.setEditable(true);
		} else if (dropDown.isEditable()) {
			// ignore because we are creating a new exercise, so no table exists yet
			// TODO: Can add functionality for miss-clicking "New Exercise"
			//       If the dropDown value exists in (arraylist) exercises, then set editable to false
		} else {
			// an exercise with data has been selected
			// so the linechart will be updated to display that data
			updateChart();

		}
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources){
		// FOR TESTING
//		String url = "jdbc:mysql://localhost:3306/test";
//		String query = "select table_name from information_schema.tables\n"
//				+ "where table_type= \"BASE TABLE\" and table_schema=\'test\';";
		String uname = "root";
		String password = "pw";
		// table_name in the database are the different exercises
		// this query retrieves all the logged exercises
		String url = "jdbc:mysql://localhost:3306/strength";
		String query = "select table_name from information_schema.tables\n"
				+ "where table_type= \"BASE TABLE\" and table_schema=\'strength\';";
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		chart.setAnimated(false); // animations messed with chart, never displayed properly
		// establishes connection with the database holding
		// logged exercises and their data
		try {
			connection = DriverManager.getConnection(url, uname, password);
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			String exerciseName;
			while (result.next()) {
				exerciseName = result.getString(1);
				exercises.add(exerciseName);
				numExercises++;
			}
			exercises.add("New Exercise");
			dropDown.setItems(FXCollections.observableArrayList(exercises));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
