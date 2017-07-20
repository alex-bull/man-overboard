

# Zaffre Tides - RaceVision (GAME X)
 
RaceVision is a Maven project to represent a boat race (such as the America's Cup).
It visualises incoming data from a server’s data stream and constantly updates until the stream finishes sending.

Gitlab CI server: https://eng-git.canterbury.ac.nz/seng302-2017/team-26.git


###Operation
To start the race, you need to select and confirm a data stream that you want to visualise. 
You can then view the race upon selecting "Go to Race View".

Toggles on the LHS of the screen enable the user to view custom annotations.

###Project Structure
 - `Visualizer/src/main/java/` Application source
 - `Visualizer/src/main/resources/` fxml, css and image resources
 - `Commons/src/main/java/` Models, parsers and utilities
 - `Mock/src/main/java` Mock Data 
 - `doc/` User and design documentation
 