# Zaffre Tides - Man Overboard
 
Man Overboard is a boat racing game inspired by the America's Cup. 
It has three components: the model (mocker), the view (visualiser) and the controller.

Gitlab CI server: https://eng-git.canterbury.ac.nz/seng302-2017/team-26.git


### Operation
Start the App. If you are running the server, also start the BoatMocker. 
The default address is localhost but another IP address may be specified if the server is on a different computer.
Press confirm and the race will start after a period of time to allow connections from others, and after a short countdown.

Controls:  
W or up arrow - angle your boat upwind  
S or down arrow - angle your boat downwind  
Q - zoom in/out  
Enter - tack or gybe  
Shift - halt your boat

Toggles on the left side of the screen enable the user to enable or disable various annotations.

### Project Structure
 - `Visualizer/src/main/java/` Application for client containing the controller and view
 - `Visualizer/src/main/resources/` Fxml, css and image resources
 - `Commons/src/main/java/` Models, parsers and utilities
 - `Mock/src/main/java` Model for the server containing datafeed and utilities
 - `doc/` User and design documentation
 
### Multiplayer Mode
 - Run more than one App instance
 - Run BoatMocker once (App instances must connect within 10 seconds)
 
### Running the jar
Navigate to team26 and run the command:
java -jar Visualizer/target/Visualizer-0.0.jar | java -jar Mock/target/Mock-0.0.jar
 