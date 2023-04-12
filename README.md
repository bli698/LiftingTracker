# LiftingTracker
JavaFX Project using FXML, and a local SQL database

In this application, the javafx application connects with a local SQL database to log and retrieve
exercise data (reps, weight, date).
Along with JavaFX inputs that are then used in a query to access the database, the retrieved data is
then plotted onto a line chart for a visual representation of the individual's progress
over time.

Before starting:  
0.1 User must have a MySQL database running on their device.  
0.2 The database I am using uses IP: localhost and database name: strength. The username and
password depend on the users SQL server configurations.

Steps to use:  
1. Upon starting the application, the tables in the database will be loaded into the drop down menu.
(If it is the first time running the application, the only option will be "New Exercise".)
2. Use the drop down menu to select the exercise. This will update the line chart unless "New Exercise" was
selected. If "New Exercise" is selected, the input box must be unselected in some way, then reselected to edit it.
(Click on another text field, then back onto the drop down menu.) Enter the name of the exercise, without spaces. (ie. Use underscores)
3. Enter a double for the weight, and an integer for reps.
4. Click the "Add" button. The new data point will be displayed onto the line chart.
